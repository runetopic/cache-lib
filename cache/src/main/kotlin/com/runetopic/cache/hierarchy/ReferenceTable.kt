package com.runetopic.cache.hierarchy

import com.runetopic.cache.compression.Compression
import com.runetopic.cache.crypto.Whirlpool
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.IFile
import com.runetopic.cache.hierarchy.index.group.file.Js5File
import com.runetopic.cache.store.js5.IDatFile
import com.runetopic.cache.store.js5.IIdxFile
import com.runetopic.cache.store.js5.impl.IdxFile
import java.nio.ByteBuffer
import java.util.*
import java.util.zip.ZipException

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
        var biggest = -1

        val groupIds = IntArray(count)

        (0 until count).forEach {
            groupIds[it] = buffer.readUnsignedShort().let { id -> lastGroupId += id; lastGroupId }
            if (groupIds[it] > biggest) biggest = groupIds[it]
        }

        val largestGroupId = biggest + 1
        val groupNameHashes = groupNameHashes(largestGroupId, count, isNamed, groupIds, buffer)
        val groupCrcs = groupCrcs(largestGroupId, count, groupIds, buffer)
        val groupWhirlpools = groupWhirlpools(largestGroupId, isUsingWhirlPool, count, buffer, groupIds)
        val groupRevisions = groupRevisions(largestGroupId, count, groupIds, buffer)
        val groupFileIds = groupFileIds(largestGroupId, count, groupIds, buffer)
        val fileIds = fileIds(largestGroupId, groupFileIds, count, groupIds, buffer)
        val fileNameHashes = fileNameHashes(largestGroupId, groupFileIds, count, groupIds, buffer, isNamed)

        val groups = hashMapOf<Int, Js5Group>()
        (0 until count).forEach {
            val groupId = groupIds[it]
            val groupReferenceTable = datFile.readReferenceTable(idxFile.id(), idxFile.loadReferenceTable(it))
            groups[it] = (Js5Group(
                id = groupId,
                nameHash = groupNameHashes[groupId],
                crc = groupCrcs[groupId],
                whirlpool = groupWhirlpools[groupId],
                revision = groupRevisions[groupId],
                keys = intArrayOf(),
                files = files(fileIds, fileNameHashes, groupReferenceTable, groupFileIds[it], it),
                data = groupReferenceTable
            ))
        }
        return Js5Index(idxFile.id(), container.crc, whirlpool, container.compression, protocol, revision, isNamed, groups)
    }

    private fun groupFileIds(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val groupFileIds = IntArray(largestGroupId)
        (0 until count).forEach {
            groupFileIds[groupIds[it]] = buffer.readUnsignedShort()
        }
        return groupFileIds
    }

    private fun groupRevisions(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
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
        groupIds: IntArray,
    ): Array<ByteArray> {
        val whirlpools = Array(largestGroupId) { ByteArray(Whirlpool.DIGESTBYTES) }
        if (usesWhirlpool.not()) return whirlpools

        (0 until count).forEach {
            val whirlpool = ByteArray(Whirlpool.DIGESTBYTES)
            buffer.get(whirlpool)
            whirlpools[groupIds[it]] = whirlpool
        }
        return whirlpools
    }

    private fun groupCrcs(
        largestGroupId: Int,
        count: Int,
        groupIds: IntArray,
        buffer: ByteBuffer,
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
        buffer: ByteBuffer,
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
        buffer: ByteBuffer
    ): Array<Array<Int>> {
        val fileIds = Array(largestGroupId) { Array(validFileIds[it]) { -1 } }
        (0 until count).forEach {
            val groupId = groupIds[it]
            var currentFileId = 0
            (0 until validFileIds[groupId]).forEach { fileId ->
                buffer.readUnsignedShort()
                    .let { id -> currentFileId += id; currentFileId }
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
    ): Array<Array<Int>> {
        val fileNameHashes = Array(largestGroupId) { Array(validFileIds[it]) { -1 } }
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

    private fun files(
        fileIds: Array<Array<Int>>,
        fileNameHashes: Array<Array<Int>>,
        groupReferenceTableData: ByteArray,
        count: Int,
        groupId: Int
    ): Map<Int, IFile> {
        if (groupReferenceTableData.isEmpty()) return hashMapOf(Pair(0, Js5File.DEFAULT))

        val src: ByteArray = try {
            Compression.decompress(groupReferenceTableData, emptyArray()).data
        } catch (exception: ZipException) {
            groupReferenceTableData
        }

        if (count == 1) {
            return hashMapOf(Pair(0, Js5File(groupId, fileIds[groupId][0], fileNameHashes[groupId][0], src)))
        }

        var position = src.size
        val chunks: Int = src[--position].toInt() and 0xFF
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
            files[it] = Js5File(groupId, fileIds[groupId][it], fileNameHashes[groupId][it], filesDatas[it])
        }
        return files
    }
}