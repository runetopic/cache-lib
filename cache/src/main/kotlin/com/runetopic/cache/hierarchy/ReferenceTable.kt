package com.runetopic.cache.hierarchy

import com.runetopic.cache.compression.Compression
import com.runetopic.cache.crypto.Whirlpool
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.Js5File
import com.runetopic.cache.store.js5.IDatFile
import com.runetopic.cache.store.js5.IIdxFile
import com.runetopic.cache.store.js5.impl.IdxFile
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal data class ReferenceTable(
    val idxFile: IdxFile,
    val id: Int,
    val sector: Int,
    val length: Int,
) {
    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.idxFile)
        hash = 19 * hash + this.id
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> {
                return false
            }
            else -> return when {
                javaClass != other.javaClass -> false
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    when {
                        idxFile != referenceTable.idxFile -> false
                        id != referenceTable.id -> false
                        sector != referenceTable.sector -> false
                        this.length != referenceTable.length -> false
                        else -> true
                    }
                }
            }
        }
    }

    fun exists(): Boolean = (length != 0 && sector != 0)

    fun loadIndex(datFile: IDatFile, idxFile: IIdxFile, whirlpool: ByteArray, data: ByteArray): Js5Index {
        val container = Compression.decompress(data, emptyArray())
        val buffer = ByteBuffer.wrap(container.data)
        val protocol = buffer.readUnsignedByte()
        var revision = 0

        if (protocol < 5 || protocol > 6) {
            throw ProtocolException("Unhandled protocol $protocol when reading index $this")
        }

        if (protocol >= 6) {
            revision = buffer.int
        }

        val hash = buffer.readUnsignedByte()
        val isNamed = (0x1 and hash) != 0
        val isUsingWhirlPool = (0x2 and hash) != 0

        val count = buffer.readUnsignedShort()

        var lastGroupId = 0
        var largestGroupId = -1

        val validGroupIds = IntArray(count)

        (0 until count).forEach {
            validGroupIds[it] = buffer.readUnsignedShort().let { id -> lastGroupId += id; lastGroupId }
            if (validGroupIds[it] > largestGroupId) largestGroupId = validGroupIds[it]
        }

        val size = largestGroupId + 1
        val nameHashes = nameHashes(size, count, isNamed, validGroupIds, buffer)
        val crcs = crcs(size, count, validGroupIds, buffer)
        val whirlpools = whirlpools(isUsingWhirlPool, count, buffer, validGroupIds)
        val revisions = revisions(size, count, validGroupIds, buffer)
        val validFileIds = validFileIds(size, count, validGroupIds, buffer)
        val files = entries(size, validFileIds, count, validGroupIds, buffer, isNamed)

        val groups = hashMapOf<Int, Js5Group>()
        (0 until count).forEach {
            groups[it] = (Js5Group(
                id = validGroupIds[it],
                nameHash = if (isNamed) nameHashes[validGroupIds[it]] else -1,
                crc = crcs[validGroupIds[it]],
                whirlpool = if (isUsingWhirlPool) whirlpools[validGroupIds[it]] else ByteArray(Whirlpool.DIGESTBYTES),
                revision = revisions[validGroupIds[it]],
                keys = intArrayOf(),
                files = files[it],
                data = datFile.readReferenceTable(idxFile.id(), idxFile.loadReferenceTable(it))
            ))
        }
        return Js5Index(idxFile.id(), container.crc, whirlpool, container.compression, protocol, revision, isNamed, groups)
    }

    private fun entries(
        size: Int,
        validFileIds: IntArray,
        count: Int,
        validGroupIds: IntArray,
        buffer: ByteBuffer,
        isNamed: Boolean,
    ): Array<Array<Js5File>> {
        val fileIds = Array(size) { Array(validFileIds[it]) { -1 } }
        val nameHashes = Array(size) { Array(validFileIds[it]) { -1 } }

        (0 until count).forEach {
            val groupId = validGroupIds[it]
            var currentFileId = 0
            (0 until validFileIds[groupId]).forEach { fileId ->
                buffer.readUnsignedShort()
                    .let { id -> currentFileId += id; currentFileId }
                    .also { fileIds[groupId][fileId] = currentFileId }
            }
        }

        if (isNamed) {
            (0 until count).forEach {
                val groupId = validGroupIds[it]
                (0 until validFileIds[groupId]).forEach { fileId ->
                    nameHashes[groupId][fileId] = buffer.int
                }
            }
        }

        val files = Array(size) { row -> Array(validFileIds[row]) { Js5File() } }
        (0 until count).forEach {
            val groupId = validGroupIds[it]
            (0 until validFileIds[groupId]).forEach { fileId ->
                files[groupId][fileId] = Js5File(groupId, fileIds[groupId][fileId], nameHashes[groupId][fileId])
            }
        }
        return files
    }

    private fun validFileIds(
        size: Int,
        count: Int,
        validGroupIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val validFileIds = IntArray(size)
        (0 until count).forEach {
            validFileIds[validGroupIds[it]] = buffer.readUnsignedShort()
        }
        return validFileIds
    }

    private fun revisions(
        size: Int,
        count: Int,
        validGroupIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val revisions = IntArray(size)
        (0 until count).forEach {
            revisions[validGroupIds[it]] = buffer.int
        }
        return revisions
    }

    private fun whirlpools(
        usesWhirlpool: Boolean,
        count: Int,
        buffer: ByteBuffer,
        validGroupIds: IntArray,
    ): Array<ByteArray> {
        val whirlpools = Array(Whirlpool.DIGESTBYTES) { byteArrayOf() }
        if (usesWhirlpool) {
            (0 until count).forEach {
                val whirlpool = ByteArray(Whirlpool.DIGESTBYTES)
                buffer.get(whirlpool)
                whirlpools[validGroupIds[it]] = whirlpool
            }
        }
        return whirlpools
    }

    private fun crcs(
        size: Int,
        count: Int,
        validGroupIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val crcs = IntArray(size)
        (0 until count).forEach {
            crcs[validGroupIds[it]] = buffer.int
        }
        return crcs
    }

    private fun nameHashes(
        size: Int,
        count: Int,
        isNamed: Boolean,
        validGroupIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val nameHashes = IntArray(size) { -1 }

        if (isNamed.not()) return nameHashes

        (0 until count).forEach {
            nameHashes[validGroupIds[it]] = buffer.int
        }
        return nameHashes
    }
}