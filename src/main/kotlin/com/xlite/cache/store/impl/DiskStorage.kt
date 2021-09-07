package com.xlite.cache.store.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.Archive
import com.xlite.cache.Index
import com.xlite.cache.file.FileConstants
import com.xlite.cache.store.IStorage
import com.xlite.cache.store.Store
import com.xlite.cache.file.IDatFile
import com.xlite.cache.file.IIndexFile
import com.xlite.cache.file.impl.DatFile
import com.xlite.cache.file.impl.IndexFile
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class DiskStorage(private val directory: File): IStorage {
    private var masterIndexFile: IIndexFile
    private var datFile: IDatFile
    private var indexFiles: ArrayList<IndexFile> = arrayListOf()
    private val logger = InlineLogger()

    init {
        val masterIndexFile = File("${directory}/${FileConstants.MAIN_FILE_255}")

        if (masterIndexFile.exists().not()) {
            throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_255} in directory ${directory}/${FileConstants.MAIN_FILE_255}")
        }

        val datFile = File("${directory}/${FileConstants.MAIN_FILE_DAT}")

        if (datFile.exists().not()){
            throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_DAT} in directory ${directory}/${FileConstants.MAIN_FILE_DAT}")
        }

        this.masterIndexFile = IndexFile(FileConstants.MAIN_INDEX_ID, masterIndexFile)
        this.datFile = DatFile(datFile)
    }

    override fun init(store: Store) {
        (0 until masterIndexFile.validIndexCount()).forEach {
            indexFiles.add(getIndexFile(it))
            store.addIndex(loadIndex(it))
        }
        logger.debug { "Loaded ${indexFiles.size} indices." }
    }

    override fun loadIndex(id: Int): Index {
        val table = masterIndexFile.loadReferenceTable(id)
        val indexData = datFile.readReferenceTable(masterIndexFile.id(), table)
        return table.loadIndex(id, indexData)
    }

    override fun readArchive(archive: Archive): ByteArray {
        val indexFile = getIndexFile(archive.indexId)
        val referenceTable = indexFile.loadReferenceTable(archive.id)
        return datFile.readReferenceTable(archive.indexId, referenceTable)
    }

    override fun readFile(id: Int, archive: Archive, data: ByteArray): ByteArray {
        return archive.decodeFileEntry(id, data)
    }

    private fun getIndexFile(id: Int): IndexFile {
        val cachedIndexFile = indexFiles.find { it.id() == id }

        if (cachedIndexFile != null) return cachedIndexFile

        val file = File("$directory/${FileConstants.MAIN_FILE_IDX}${id}")

        if (file.exists().not()) {
            throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_IDX} in directory $directory")
        }

        return IndexFile(id, file)
    }

    override fun close() {
        masterIndexFile.close()
        datFile.close()
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}