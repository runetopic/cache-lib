package com.xlite.cache.service.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.constant.FileConstants
import com.xlite.cache.constant.FileConstants.MAIN_FILE_255
import com.xlite.cache.constant.FileConstants.MAIN_INDEX_ID
import com.xlite.cache.exception.ProtocolException
import com.xlite.cache.extension.readBigSmart
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.fs.Archive
import com.xlite.cache.fs.Index
import com.xlite.cache.fs.compression.Compression
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
class CacheServiceRS2(private val directory: String): ICacheService {
    private val data = getData()
    private val mainIndex = getMainIndex()

    private val logger = InlineLogger()

    override fun getMainIndex(): IndexFile {
        val file = File("${directory}${MAIN_FILE_255}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_255 in directory $directory")
        return IndexFile(MAIN_INDEX_ID, file)
    }

    override fun getData(): DataFile {
        val file = File("${directory}${FileConstants.MAIN_FILE_DAT}")
        if (file.exists().not()) throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_DAT} in directory $directory")
        return DataFile(file)
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
        val isNamed = (1 and hash) != 0

        if (hash and 1.inv() != 0 || hash and 3.inv() != 0) {
            throw ProtocolException("Unknown flag in hash read.")
        }

        val validArchivesCount = if (protocol >= 7) buffer.readBigSmart() else buffer.readUnsignedShort()

        val index = Index(protocol, revision, isNamed, validArchivesCount, ArrayList())

        (0 until validArchivesCount).forEach { _ ->
            val archive = Archive(if (protocol >= 7) buffer.readBigSmart() else buffer.readUnsignedShort())
            index.archives.add(archive)
        }

        if (isNamed) {
            (0 until validArchivesCount).forEach { archiveId ->
                val archive = index.archives[archiveId]
                archive.nameHash = buffer.int
            }
        }

        (0 until validArchivesCount).forEach { archiveId ->
            val archive = index.archives[archiveId]
            archive.crc = buffer.int
        }

        (0 until validArchivesCount).forEach { archiveId ->
            val archive = index.archives[archiveId]
            archive.revision = buffer.int
        }

        index.archives.forEach { logger.debug { it.nameHash } }
        return index
    }

    override fun close() {
        data.close()
        mainIndex.close()
    }
}