package com.runetopic.cache.hierarchy

import com.runetopic.cache.codec.Container
import com.runetopic.cache.codec.decompress
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedIntShortSmart
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.File
import com.runetopic.cache.hierarchy.index.group.file.Js5File
import com.runetopic.cache.store.storage.js5.IDatFile
import com.runetopic.cache.store.storage.js5.IIdxFile
import java.nio.ByteBuffer
import java.util.*
import java.util.zip.ZipException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal data class ReferenceTable(
    val idxFile: IIdxFile,
    val id: Int,
    val sector: Int,
    val length: Int
) {
    fun exists(): Boolean = (length != 0 && sector != 0)

    fun loadIndex(
        datFile: IDatFile,
        idxFile: IIdxFile,
        whirlpool: ByteArray,
        decompressed: Container
    ): Js5Index {
        val buffer = ByteBuffer.wrap(decompressed.data)
        val crc = decompressed.crc
        val compressionType = decompressed.compression
        val protocol = buffer.readUnsignedByte()
        val revision = when {
            protocol < 5 || protocol > 7 -> throw ProtocolException("Unhandled protocol $protocol when reading index $this")
            protocol >= 6 -> buffer.int
            else -> 0
        }
        val hash = buffer.readUnsignedByte()
        val count = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()
        val groupTables = mutableListOf<ByteArray>()
        (0 until count).forEach {
            groupTables.add(datFile.readReferenceTable(idxFile.id(), idxFile.loadReferenceTable(it)))
        }
        return loadIndexContents(idxFile.id(), buffer, crc, compressionType, revision, protocol, hash, count, whirlpool, groupTables)
    }

    private fun loadIndexContents(
        indexId: Int,
        buffer: ByteBuffer,
        crc: Int,
        compressionType: Int,
        revision: Int,
        protocol: Int,
        hash: Int,
        count: Int,
        whirlpool: ByteArray,
        groupTables: List<ByteArray>
    ): Js5Index {
        val isNamed = (0x1 and hash) != 0
        val isUsingWhirlpool = (0x2 and hash) != 0

        val groupIds = IntArray(count)
        var lastGroupId = 0
        var biggest = -1
        (0 until count).forEach {
            groupIds[it] = if (protocol >= 7) { buffer.readUnsignedIntShortSmart() } else { buffer.readUnsignedShort() }
                .let { id -> lastGroupId += id; lastGroupId }
            if (groupIds[it] > biggest) biggest = groupIds[it]
        }

        val largestGroupId = biggest + 1
        val groupNameHashes = groupNameHashes(largestGroupId, count, isNamed, groupIds, buffer)
        val groupCrcs = groupCrcs(largestGroupId, count, groupIds, buffer)
        val groupWhirlpools = groupWhirlpools(largestGroupId, isUsingWhirlpool, count, buffer, groupIds)
        val groupRevisions = groupRevisions(largestGroupId, count, groupIds, buffer)
        val groupFileIds = groupFileIds(largestGroupId, count, groupIds, buffer, protocol)

        val fileIds = fileIds(largestGroupId, groupFileIds, count, groupIds, buffer, protocol)
        val fileNameHashes = fileNameHashes(largestGroupId, groupFileIds, count, groupIds, buffer, isNamed)

        val groups = hashMapOf<Int, Js5Group>()
        (0 until count).forEach {
            val groupId = groupIds[it]
            groups[it] = (Js5Group(
                groupId,
                groupNameHashes[groupId],
                groupCrcs[groupId],
                groupWhirlpools[groupId],
                groupRevisions[groupId],
                intArrayOf(),//TODO
                groupFiles(fileIds, fileNameHashes, groupTables[it], groupFileIds[it], it),
                groupTables[it]
            ))
        }
        return Js5Index(indexId, crc, whirlpool, compressionType, protocol, revision, isNamed, groups)
    }

    private fun groupFileIds(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        protocol: Int
    ): IntArray {
        val groupFileIds = IntArray(largestGroupId)
        (0 until count).forEach {
            groupFileIds[groupIds[it]] = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()
        }
        return groupFileIds
    }

    private fun groupRevisions(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        val revisions = IntArray(largestGroupId)
        (0 until count).forEach {
            revisions[groupIds[it]] = buffer.int
        }
        return revisions
    }

    private fun groupWhirlpools(
        largestGroupId: Int,
        usesWhirlpool: Boolean,
        count: Int,
        buffer: ByteBuffer,
        groupIds: IntArray
    ): Array<ByteArray> {
        val whirlpools = Array(largestGroupId) { ByteArray(64) }
        if (usesWhirlpool.not()) return whirlpools

        (0 until count).forEach {
            val whirlpool = ByteArray(64)
            buffer.get(whirlpool)
            whirlpools[groupIds[it]] = whirlpool
        }
        return whirlpools
    }

    private fun groupCrcs(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        val crcs = IntArray(largestGroupId)
        (0 until count).forEach {
            crcs[groupIds[it]] = buffer.int
        }
        return crcs
    }

    private fun groupNameHashes(
        largestGroupId: Int,
        count: Int,
        isNamed: Boolean,
        groupIds: IntArray,
        buffer: ByteBuffer
    ): IntArray {
        val nameHashes = IntArray(largestGroupId) { -1 }
        if (isNamed.not()) return nameHashes

        (0 until count).forEach {
            nameHashes[groupIds[it]] = buffer.int
        }
        return nameHashes
    }

    private fun fileIds(
        largestGroupId: Int,
        validFileIds: IntArray,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        protocol: Int
    ): Array<IntArray> {
        val fileIds = Array(largestGroupId) { IntArray(validFileIds[it]) }
        (0 until count).forEach {
            val groupId = groupIds[it]
            var currentFileId = 0
            (0 until validFileIds[groupId]).forEach { fileId ->
                if (protocol >= 7) { buffer.readUnsignedIntShortSmart() } else { buffer.readUnsignedShort() }
                    .let { i -> currentFileId += i; currentFileId }
                    .also { fileIds[groupId][fileId] = currentFileId }
            }
        }
        return fileIds
    }

    private fun fileNameHashes(
        largestGroupId: Int,
        validFileIds: IntArray,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
        isNamed: Boolean
    ): Array<IntArray> {
        val fileNameHashes = Array(largestGroupId) { IntArray(validFileIds[it]) }
        if (isNamed) {
            (0 until count).forEach {
                val groupId = groupIds[it]
                (0 until validFileIds[groupId]).forEach { fileId ->
                    fileNameHashes[groupId][fileId] = buffer.int
                }
            }
        }
        return fileNameHashes
    }

    private fun groupFiles(
        fileIds: Array<IntArray>,
        fileNameHashes: Array<IntArray>,
        groupReferenceTableData: ByteArray,
        count: Int,
        groupId: Int
    ): Map<Int, File> {
        if (groupReferenceTableData.isEmpty()) return hashMapOf(Pair(0, Js5File.DEFAULT))

        val src: ByteArray = try {
            groupReferenceTableData.decompress()
        } catch (exception: ZipException) {
            groupReferenceTableData
        }

        if (count == 1) {
            return hashMapOf(Pair(0, Js5File(fileIds[groupId][0], fileNameHashes[groupId][0], src)))
        }

        var position = src.size
        val chunks = src[--position].toInt() and 0xFF
        position -= chunks * (count * 4)
        val buffer = ByteBuffer.wrap(src)
        buffer.position(position)
        val filesSizes = IntArray(count)
        (0 until chunks).forEach { _ ->
            var read = 0
            (0 until count).forEach {
                read += buffer.int
                filesSizes[it] += read
            }
        }
        val filesDatas = Array(count) { byteArrayOf() }
        (0 until count).forEach {
            filesDatas[it] = ByteArray(filesSizes[it])
            filesSizes[it] = 0
        }
        buffer.position(position)
        var offset = 0
        (0 until chunks).forEach { _ ->
            var read = 0
            (0 until count).forEach {
                read += buffer.int
                System.arraycopy(src, offset, filesDatas[it], filesSizes[it], read)
                offset += read
                filesSizes[it] += read
            }
        }

        val files = hashMapOf<Int, Js5File>()
        (0 until count).forEach {
            files[it] = Js5File(fileIds[groupId][it], fileNameHashes[groupId][it], filesDatas[it])
        }
        return files
    }

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
            null -> return false
            else -> return when {
                javaClass != other.javaClass -> false
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    when {
                        idxFile != referenceTable.idxFile -> false
                        id != referenceTable.id -> false
                        sector != referenceTable.sector -> false
                        length != referenceTable.length -> false
                        else -> true
                    }
                }
            }
        }
    }
}