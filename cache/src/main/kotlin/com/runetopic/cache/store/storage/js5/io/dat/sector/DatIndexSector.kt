package com.runetopic.cache.store.storage.js5.io.dat.sector

import com.runetopic.cache.codec.ContainerCodec
import com.runetopic.cache.codec.decompress
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.*
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.hierarchy.index.group.Group
import com.runetopic.cache.store.storage.js5.io.dat.DatFileCodec
import com.runetopic.cache.store.storage.js5.io.dat.DatSectorCodec
import com.runetopic.cache.store.storage.js5.io.idx.IdxFileCodec
import com.runetopic.cryptography.toWhirlpool
import java.nio.ByteBuffer
import java.util.zip.ZipException

/**
 * @author Jordan Abraham
 */
internal data class DatIndexSector(
    val datFile: DatFileCodec,
    val idxFile: IdxFileCodec,
    val data: ByteArray
) : DatSectorCodec<Index> {

    override fun decode(): Index {
        val decompressed = ContainerCodec.decompress(data)
        val buffer = decompressed.data.toByteBuffer()
        val protocol = buffer.readUnsignedByte()
        val revision = when {
            protocol < 5 || protocol > 7 -> throw ProtocolException("Unhandled protocol $protocol")
            protocol >= 6 -> buffer.int
            else -> 0
        }
        val mask = buffer.readUnsignedByte()
        val count = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()

        val isNamed = (0x1 and mask) != 0
        val isUsingWhirlpool = (0x2 and mask) != 0

        val groupIds = decodeGroupIds(count, buffer, protocol)
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
        val groupNameHashes = decodeGroupNameHashes(maxGroupId, count, isNamed, groupIds, buffer)
        val groupCrcs = decodeGroupCrcs(maxGroupId, count, groupIds, buffer)
        val groupWhirlpools = decodeGroupWhirlpools(maxGroupId, isUsingWhirlpool, count, buffer, groupIds)
        val groupRevisions = decodeGroupRevisions(maxGroupId, count, groupIds, buffer)
        val groupFileSizes = decodeGroupFileSizes(maxGroupId, count, groupIds, buffer, protocol)
        val groupFileIds = decodeGroupFileIds(maxGroupId, groupFileSizes, count, groupIds, buffer, protocol)
        val groupFileNameHashes = decodeGroupFileNameHashes(maxGroupId, groupFileSizes, count, groupIds, buffer, isNamed)

        val groups = hashMapOf<Int, Group>()
        repeat(count) {
            val groupId = groupIds[it]

            val groupReferenceTableData = datFile.decode(idxFile.id(), idxFile.decode(groupId))
            val data = if (groupReferenceTableData.isEmpty()) byteArrayOf() else try {
                groupReferenceTableData.decompress()
            } catch (exception: ZipException) {
                groupReferenceTableData
            }

            val groupSector = DatGroupSector(
                groupFileIds,
                groupFileNameHashes,
                data,
                groupFileSizes[groupId],
                groupId
            )

            groups[groupId] = Group(
                groupId,
                groupNameHashes[groupId],
                groupCrcs[groupId],
                groupWhirlpools[groupId],
                groupRevisions[groupId],
                intArrayOf(), // TODO
                groupSector.decode().toMutableMap(),
                data
            )
        }

        return Index(
            idxFile.id(),
            decompressed.crc,
            data.toWhirlpool(),
            decompressed.compression,
            protocol,
            revision,
            isNamed,
            isUsingWhirlpool,
            groups
        )
    }

    override fun encode(override: Index): ByteArray {
        val count = override.groups().size
        val header = ByteBuffer.allocate(if (override.protocol >= 7) if (count >= Short.MAX_VALUE) 10 else 8 else if (override.protocol >= 6) 8 else 4)
        header.put(override.protocol.toByte())
        if (override.protocol >= 6) {
            header.putInt(override.revision)
        }

        val mask = when {
            override.isNamed -> 0x1
            override.isUsingWhirlpool -> 0x2
            else -> 0x0
        }
        header.put(mask.toByte())

        if (override.protocol >= 7) header.putIntShortSmart(count) else header.putShort(count.toShort())

        val ids = override.groups().stream().map { it.id }.toList().toIntArray()
        val max = (ids.maxOrNull() ?: -1) + 1
        val nameHashes = IntArray(max) { override.group(it).nameHash }
        val crcs = IntArray(max) { override.group(it).crc }
        val whirlpools = Array(max) { override.group(it).whirlpool }
        val revisions = IntArray(max) { override.group(it).revision }
        val fileSizes = IntArray(max) { override.group(it).fileIds().size }
        val fileIds = Array(max) { override.groups().stream().filter { group -> group.id == it }.findFirst().takeIf { it.isPresent }?.get()?.fileIds()?.map { it }?.toList()?.toIntArray() ?: intArrayOf() }
        val fileNameHashes = Array(max) { override.groups().stream().filter { group -> group.id == it }.findFirst().takeIf { it.isPresent }?.get()?.files()?.map { it.nameHash }?.toList()?.toIntArray() ?: intArrayOf() }

        val groupIds = encodeGroupIds(count, override.protocol, ids)
        val groupNameHashes = encodeGroupNameHashes(count, override.isNamed, ids, nameHashes)
        val groupCrcs = encodeGroupCrcs(count, ids, crcs)
        val groupWhirlpools = encodeGroupWhirlpools(count, ids, override.isUsingWhirlpool, whirlpools)
        val groupRevisions = encodeGroupRevisions(count, ids, revisions)
        val groupFileSizes = encodeGroupFileSizes(count, ids, override.protocol, fileSizes)
        val groupFileIds = encodeGroupFileIds(count, ids, override.protocol, fileSizes, fileIds)
        val groupFileNameHashes = encodeGroupFileNameHashes(count, ids, fileSizes, override.isNamed, fileNameHashes)

        val buffer = ByteBuffer.allocate(
            /**/header.position() +
                groupIds.position() +
                groupNameHashes.position() +
                groupCrcs.position() +
                groupWhirlpools.position() +
                groupRevisions.position() +
                groupFileSizes.position() +
                groupFileIds.position() +
                groupFileNameHashes.position()
        )

        buffer.put(header.array())
        buffer.put(groupIds.array())
        buffer.put(groupNameHashes.array())
        buffer.put(groupCrcs.array())
        buffer.put(groupWhirlpools.array())
        buffer.put(groupRevisions.array())
        buffer.put(groupFileSizes.array())
        buffer.put(groupFileIds.array())
        buffer.put(groupFileNameHashes.array())

        val compressed = ContainerCodec.compress(
            override.compression,
            override.revision,
            buffer.array(),
            intArrayOf() // TODO
        )
        return compressed.array()
    }

    fun decodeGroupIds(
        count: Int,
        buffer: ByteBuffer,
        protocol: Int
    ): IntArray {
        return IntArray(count).also {
            repeat(count) { index ->
                it[index] = (if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()) + if (index == 0) 0 else it[index - 1]
            }
        }
    }

    fun encodeGroupIds(
        count: Int,
        protocol: Int,
        groupIds: IntArray
    ): ByteBuffer {
        // TODO This buffer needs to be allocated properly for protocol >= 7
        return ByteBuffer.allocate(calc(count, groupIds, protocol)).also {
            repeat(count) { index ->
                val value = groupIds[index] - if (index == 0) 0 else groupIds[index - 1]
                if (protocol >= 7) it.putIntShortSmart(value) else it.putShort(value.toShort())
            }
        }
    }

    fun calc(
        count: Int,
        groupIds: IntArray,
        protocol: Int
    ): Int {
        var bytes = 0
        repeat(count) {
            val value = groupIds[it] - if (it == 0) 0 else groupIds[it - 1]
            bytes += if (protocol >= 7) if (value >= Short.MAX_VALUE) 4 else 2 else 2
        }
        return bytes
    }

    fun decodeGroupFileSizes(
        maxGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        protocol: Int
    ): IntArray {
        return IntArray(maxGroupId).also {
            repeat(count) { index ->
                it[groupIds[index]] = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()
            }
        }
    }

    fun encodeGroupFileSizes(
        count: Int,
        groupIds: IntArray,
        protocol: Int,
        groupFileSizes: IntArray
    ): ByteBuffer {
        // TODO This buffer needs to be allocated properly for protocol >= 7
        return ByteBuffer.allocate(groupIds.sumOf { Short.SIZE_BYTES }).also {
            repeat(count) { index ->
                if (protocol >= 7) it.putIntShortSmart(groupFileSizes[groupIds[index]]) else it.putShort(groupFileSizes[groupIds[index]].toShort())
            }
        }
    }

    fun decodeGroupRevisions(
        maxGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        return IntArray(maxGroupId).also {
            repeat(count) { index ->
                it[groupIds[index]] = buffer.int
            }
        }
    }

    fun encodeGroupRevisions(
        count: Int,
        groupIds: IntArray,
        revisions: IntArray
    ): ByteBuffer {
        return ByteBuffer.allocate(count * Int.SIZE_BYTES).also {
            repeat(count) { index ->
                it.putInt(revisions[groupIds[index]])
            }
        }
    }

    fun decodeGroupWhirlpools(
        maxGroupId: Int,
        usesWhirlpool: Boolean,
        count: Int,
        buffer: ByteBuffer,
        groupIds: IntArray
    ): Array<ByteArray> {
        return Array(maxGroupId) { ByteArray(64) }.also {
            if (usesWhirlpool) {
                repeat(count) { index ->
                    it[groupIds[index]] = ByteArray(64).also { b -> buffer.get(b) }
                }
            }
        }
    }

    fun encodeGroupWhirlpools(
        count: Int,
        groupIds: IntArray,
        usesWhirlpool: Boolean,
        whirlpools: Array<ByteArray>
    ): ByteBuffer {
        return if (usesWhirlpool.not()) ByteBuffer.allocate(0) else ByteBuffer.allocate(count * 64).also {
            repeat(count) { index ->
                it.put(whirlpools[groupIds[index]])
            }
        }
    }

    fun decodeGroupCrcs(
        maxGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        return IntArray(maxGroupId).also {
            repeat(count) { index ->
                it[groupIds[index]] = buffer.int
            }
        }
    }

    fun encodeGroupCrcs(
        count: Int,
        groupIds: IntArray,
        crcs: IntArray
    ): ByteBuffer {
        return ByteBuffer.allocate(count * Int.SIZE_BYTES).also {
            repeat(count) { index ->
                it.putInt(crcs[groupIds[index]])
            }
        }
    }

    fun decodeGroupNameHashes(
        maxGroupId: Int,
        count: Int,
        isNamed: Boolean,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        return IntArray(maxGroupId) { -1 }.also {
            if (isNamed) {
                repeat(count) { index ->
                    it[groupIds[index]] = buffer.int
                }
            }
        }
    }

    fun encodeGroupNameHashes(
        count: Int,
        isNamed: Boolean,
        groupIds: IntArray,
        nameHashes: IntArray
    ): ByteBuffer {
        return if (isNamed.not()) ByteBuffer.allocate(0) else ByteBuffer.allocate(count * Int.SIZE_BYTES).also {
            repeat(count) { index ->
                it.putInt(nameHashes[groupIds[index]])
            }
        }
    }

    fun decodeGroupFileIds(
        maxGroupId: Int,
        groupFileSizes: IntArray,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        protocol: Int
    ): Array<IntArray> {
        return Array(maxGroupId) { IntArray(groupFileSizes[it]) }.also {
            repeat(count) { index ->
                groupIds[index].let { groupId ->
                    repeat(groupFileSizes[groupId]) { fileId ->
                        it[groupId][fileId] = (if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()) + if (fileId == 0) 0 else it[groupId][fileId - 1]
                    }
                }
            }
        }
    }

    fun encodeGroupFileIds(
        count: Int,
        groupIds: IntArray,
        protocol: Int,
        groupFileSizes: IntArray,
        fileIds: Array<IntArray>
    ): ByteBuffer {
        // TODO This buffer needs to be allocated properly for protocol >= 7
        return ByteBuffer.allocate(groupFileSizes.sumOf { it * Short.SIZE_BYTES }).also {
            repeat(count) { index ->
                groupIds[index].let { groupId ->
                    repeat(groupFileSizes[groupId]) { fileId ->
                        val value = fileIds[groupId][fileId] - if (fileId == 0) 0 else fileIds[groupId][fileId - 1]
                        if (protocol >= 7) it.putIntShortSmart(value) else it.putShort(value.toShort())
                    }
                }
            }
        }
    }

    fun decodeGroupFileNameHashes(
        maxGroupId: Int,
        groupFileSizes: IntArray,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        isNamed: Boolean
    ): Array<IntArray> {
        return Array(maxGroupId) { IntArray(groupFileSizes[it]) { -1 } }.also {
            if (isNamed) {
                repeat(count) { index ->
                    groupIds[index].let { groupId ->
                        repeat(groupFileSizes[groupId]) { fileId ->
                            it[groupId][fileId] = buffer.int
                        }
                    }
                }
            }
        }
    }

    fun encodeGroupFileNameHashes(
        count: Int,
        groupIds: IntArray,
        groupFileSizes: IntArray,
        isNamed: Boolean,
        fileNameHashes: Array<IntArray>
    ): ByteBuffer {
        return if (isNamed.not()) ByteBuffer.allocate(0) else ByteBuffer.allocate(groupFileSizes.sum() * Int.SIZE_BYTES).also {
            repeat(count) { index ->
                groupIds[index].let { groupId ->
                    repeat(groupFileSizes[groupId]) { fileId ->
                        it.putInt(fileNameHashes[groupId][fileId])
                    }
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatIndexSector

        if (datFile != other.datFile) return false
        if (idxFile != other.idxFile) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = datFile.hashCode()
        result = 31 * result + idxFile.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
