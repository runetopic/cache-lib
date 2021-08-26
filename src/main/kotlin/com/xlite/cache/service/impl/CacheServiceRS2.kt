package com.xlite.cache.service.impl

import com.xlite.cache.constant.FileConstants
import com.xlite.cache.constant.FileConstants.MAIN_FILE_255
import com.xlite.cache.constant.FileConstants.MAIN_FILE_DAT
import com.xlite.cache.constant.FileConstants.MAIN_FILE_IDX
import com.xlite.cache.constant.FileConstants.MAIN_INDEX_ID
import com.xlite.cache.exception.ProtocolException
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.fs.Archive
import com.xlite.cache.fs.Index
import com.xlite.cache.fs.compression.Compression
import com.xlite.cache.fs.file.FileEntry
import com.xlite.cache.fs.file.impl.DataFile
import com.xlite.cache.fs.file.impl.IndexFile
import com.xlite.cache.service.ICacheService
import java.io.File
import java.io.FileNotFoundException
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class CacheServiceRS2(private val directory: String) : ICacheService {
    private val data = getData()
    private val mainIndex = getMainIndex()

    override fun getMainIndex(): IndexFile {
        val file = File("${directory}${MAIN_FILE_255}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_255 in directory $directory")
        return IndexFile(MAIN_INDEX_ID, file)
    }

    override fun getData(): DataFile {
        val file = File("${directory}${FileConstants.MAIN_FILE_DAT}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_DAT in directory $directory")
        return DataFile(file)
    }

    override fun getIndexFiles(): List<IndexFile> {
        val indexFiles = arrayListOf<IndexFile>()
        val validIndexCount = mainIndex.validIndexCount()
        for (index in 0 until validIndexCount) {
            val file = File("${directory}${MAIN_FILE_IDX}${index}")

            if (!file.exists()) {
                throw FileNotFoundException("Missing ${MAIN_FILE_IDX}${index} in directory $directory")
            }

            val indexFile = IndexFile(index, file)
            indexFiles.add(indexFile)
        }

        return indexFiles
    }

    override fun readReferenceTable(id: Int): ByteArray {
        val table = mainIndex.loadReferenceTable(id)
        return data.readReferenceTable(mainIndex.id(), table)
    }

    override fun readIndex(id: Int): Index {
        val indexData = readReferenceTable(id)
        val decompressed = Compression.decompress(indexData, emptyArray())
        val buffer = ByteBuffer.wrap(decompressed.data)

        var revision = 0
        val protocol = buffer.readUnsignedByte()

        if (protocol < 5 || protocol > 7) {
            throw ProtocolException("Unhandled protocol $protocol when reading index $id")
        }

        if (protocol >= 6) {
            revision = buffer.int
        }

        val hash = buffer.readUnsignedByte()
        val isNamed = (0x1 and hash) != 0
        val usesWhirlpool = (0x2 and hash) != 0
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
        val whirlpools = readWhirlpool(usesWhirlpool, validArchivesCount, buffer, validArchiveIds)
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
                if (usesWhirlpool) whirlpools[validArchiveIds[realArchiveId]] else byteArrayOf(),
                revisions[validArchiveIds[realArchiveId]],
                intArrayOf(),
                files[realArchiveId]
            ))
        }

        return Index(id, protocol, revision, isNamed, archives.toList())
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

    override fun readArchive(archive: Archive): ByteArray {
        val index = readIndex(archive.indexId)
        val indexFile = getIndexFiles()[index.id]
        val referenceTable = indexFile.loadReferenceTable(archive.id)
        return data.readReferenceTable(index.id, referenceTable)
    }

    override fun close() {
        data.close()
        mainIndex.close()
    }
}