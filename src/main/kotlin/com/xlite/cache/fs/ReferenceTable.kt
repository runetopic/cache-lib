package com.xlite.cache.fs

import com.xlite.cache.exception.ProtocolException
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.fs.compression.Compression
import com.xlite.cache.fs.file.FileEntry
import com.xlite.cache.fs.file.impl.IndexFile
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class ReferenceTable(
    val indexFile: IndexFile,
    val archiveId: Int,
    val sector: Int,
    val length: Int
) {
    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.indexFile)
        hash = 19 * hash + this.archiveId
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> {
                return false
            }
            else -> when {
                javaClass != other.javaClass -> {
                    return false
                }
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    return when {
                        indexFile != referenceTable.indexFile -> {
                            false
                        }
                        archiveId != referenceTable.archiveId -> {
                            false
                        }
                        sector != referenceTable.sector -> {
                            false
                        }
                        this.length != referenceTable.length -> {
                            false
                        }
                        else -> true
                    }
                }
            }
        }
    }

    fun loadIndex(id: Int, indexData: ByteArray): Index {
        val container = Compression.decompress(indexData, emptyArray())
        val buffer = ByteBuffer.wrap(container.data)
        val protocol = buffer.readUnsignedByte()
        var revision = 0

        if (protocol < 5 || protocol > 7) {
            throw ProtocolException("Unhandled protocol $protocol when reading index $this")
        }

        if (protocol >= 6) {
            revision = buffer.int
        }

        val hash = buffer.readUnsignedByte()
        val isNamed = (0x1 and hash) != 0
        val isUsingWhirlPool = (0x2 and hash) != 0
        if (hash and 0x1.inv() != 0 || hash and 0x3.inv() != 0) {
            throw ProtocolException("Unknown flag in hash read.")
        }

        val validArchivesCount = buffer.readUnsignedShort()

        var lastArchiveId = 0
        var biggestArchiveId = -1

        val validArchiveIds = IntArray(validArchivesCount)

        for (index in 0 until validArchivesCount) {
            validArchiveIds[index] = buffer.readUnsignedShort().let { lastArchiveId += it; lastArchiveId }
            if (validArchiveIds[index] > biggestArchiveId) biggestArchiveId = validArchiveIds[index]
        }

        val anIntArray2107 = IntArray(biggestArchiveId + 1)
        val nameHashes = readNameHashes(biggestArchiveId, validArchivesCount, isNamed, validArchiveIds, buffer)
        val crcs = readCRCS(biggestArchiveId, validArchivesCount, validArchiveIds, buffer)
        val whirlpools = readWhirlpool(isUsingWhirlPool, validArchivesCount, buffer, validArchiveIds)
        val revisions = readRevisions(biggestArchiveId, validArchivesCount, validArchiveIds, buffer)
        val validFileIds = readFileIds(biggestArchiveId, validArchivesCount, validArchiveIds, buffer)
        val files = readFiles(biggestArchiveId, validFileIds, validArchivesCount, validArchiveIds, buffer, anIntArray2107)

        mapFileNameHashes(biggestArchiveId,
            validFileIds,
            isNamed,
            validArchivesCount,
            validArchiveIds,
            anIntArray2107,
            files,
            buffer)

        val archives = mutableListOf<Archive>()

        for (realArchiveId in 0 until validArchivesCount) {
            archives.add(Archive(
                realArchiveId,
                id,
                if (isNamed) nameHashes[validArchiveIds[realArchiveId]] else -1,
                crcs[validArchiveIds[realArchiveId]],
                if (isUsingWhirlPool) whirlpools[validArchiveIds[realArchiveId]] else byteArrayOf(),
                revisions[validArchiveIds[realArchiveId]],
                intArrayOf(),
                files[realArchiveId]
            ))
        }

        return Index(id, protocol, revision, isNamed, archives)
    }


    private fun mapFileNameHashes(
        biggestArchiveId: Int,
        validFileIds: IntArray,
        isNamed: Boolean,
        validArchivesCount: Int,
        validArchiveIds: IntArray,
        anIntArray2107: IntArray,
        fileEntries: Array<Array<FileEntry>>,
        buffer: ByteBuffer,
    ) {
        val fileNameHashes = Array(biggestArchiveId + 1) { row -> Array(validFileIds[row]) { -1 } }

        if (!isNamed) return

        for (index in 0 until validArchivesCount) {
            val archiveId = validArchiveIds[index]
            for (i_22_ in 0 until anIntArray2107[archiveId]) {
                fileNameHashes[archiveId][i_22_] = -1
            }
            for (count in 0 until validFileIds[archiveId]) {
                val fileId: Int = if (fileEntries.isEmpty()) {
                    fileEntries[validArchiveIds[index]][count].id
                } else {
                    count
                }
                fileNameHashes[index][fileId] = buffer.int
                fileEntries[archiveId][fileId].nameHash = fileNameHashes[index][fileId]
            }
        }
    }

    private fun readFiles(
        biggestArchiveId: Int,
        validFileIds: IntArray,
        validArchivesCount: Int,
        validArchiveIds: IntArray,
        buffer: ByteBuffer,
        anIntArray2107: IntArray,
    ): Array<Array<FileEntry>> {
        val files = Array(biggestArchiveId + 1) { row -> Array(validFileIds[row]) { FileEntry() } }
        var lastArchiveId1: Int
        for (index in 0 until validArchivesCount) {
            val archiveId = validArchiveIds[index]
            val validFileCount = validFileIds[archiveId]
            lastArchiveId1 = 0
            files[archiveId] = Array(validFileCount) { FileEntry() }
            var currFileId = -1
            var fileId = 0

            while (validFileCount > fileId) {
                val lastFileId: Int = buffer.readUnsignedShort().let { lastArchiveId1 += it; lastArchiveId1 }
                    .also { files[archiveId][fileId] = FileEntry(lastArchiveId1) }
                if (currFileId < lastFileId) currFileId = lastFileId
                fileId++
            }
            anIntArray2107[archiveId] = currFileId + 1
            if (validFileCount == currFileId + 1) files[archiveId] = arrayOf(FileEntry())
        }
        return files
    }

    private fun readFileIds(
        biggestArchiveId: Int,
        validArchivesCount: Int,
        validArchiveIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val validFileIds = IntArray(biggestArchiveId + 1)
        for (index in 0 until validArchivesCount) {
            validFileIds[validArchiveIds[index]] = buffer.readUnsignedShort()
        }
        return validFileIds
    }

    private fun readRevisions(
        biggestArchiveId: Int,
        validArchivesCount: Int,
        validArchiveIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val revisions = IntArray(biggestArchiveId + 1)
        for (index in 0 until validArchivesCount) {
            revisions[validArchiveIds[index]] = buffer.int
        }
        return revisions
    }

    private fun readWhirlpool(
        usesWhirlpool: Boolean,
        validArchivesCount: Int,
        buffer: ByteBuffer,
        validArchiveIds: IntArray,
    ): Array<ByteArray> {
        val whirlpools = arrayOf(byteArrayOf())
        if (usesWhirlpool) {
            for (index in 0 until validArchivesCount) {
                val b = ByteArray(64)
                buffer.get(b, 0, 64)
                whirlpools[validArchiveIds[index]] = b
            }
        }
        return whirlpools
    }

    private fun readCRCS(
        biggestArchiveId: Int,
        validArchivesCount: Int,
        validArchiveIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val crcs = IntArray(biggestArchiveId + 1)
        for (index in 0 until validArchivesCount) {
            crcs[validArchiveIds[index]] = buffer.int
        }
        return crcs
    }

    private fun readNameHashes(
        biggestArchiveId: Int,
        validArchivesCount: Int,
        isNamed: Boolean,
        validArchiveIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val nameHashes = IntArray(biggestArchiveId + 1) { -1 }

        if (isNamed.not()) return nameHashes

        for (index in 0 until validArchivesCount) {
            nameHashes[validArchiveIds[index]] = buffer.int
        }

        return nameHashes
    }
}