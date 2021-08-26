package com.xlite.cache.service.impl

import com.xlite.cache.constant.FileConstants.MAIN_FILE_255
import com.xlite.cache.constant.FileConstants.MAIN_FILE_DAT
import com.xlite.cache.constant.FileConstants.MAIN_FILE_IDX
import com.xlite.cache.constant.FileConstants.MAIN_INDEX_ID
import com.xlite.cache.fs.Archive
import com.xlite.cache.fs.Index
import com.xlite.cache.fs.file.impl.DataFile
import com.xlite.cache.fs.file.impl.IndexFile
import com.xlite.cache.service.ICacheService
import java.io.File
import java.io.FileNotFoundException

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
        val file = File("${directory}$MAIN_FILE_DAT")
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

    override fun getIndex(id: Int): Index {
        val table = mainIndex.loadReferenceTable(id)
        val indexData = data.readReferenceTable(mainIndex.id(), table)
        return table.loadIndex(id, indexData)
    }

    override fun readArchive(archive: Archive): ByteArray {
        val index = getIndex(archive.indexId)
        val indexFile = getIndexFiles()[index.id]
        val referenceTable = indexFile.loadReferenceTable(archive.id)
        return data.readReferenceTable(index.id, referenceTable)
    }

    override fun close() {
        data.close()
        mainIndex.close()
    }
}