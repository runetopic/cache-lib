package com.xlite.cache.fs.store.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.constant.FileConstants
import com.xlite.cache.fs.file.IDatFile
import com.xlite.cache.fs.file.IIndexFile
import com.xlite.cache.fs.file.impl.DatFile
import com.xlite.cache.fs.file.impl.IndexFile
import com.xlite.cache.fs.store.IStorage
import com.xlite.cache.fs.store.Store
import java.io.File
import java.io.FileNotFoundException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class DiskStorage(private val directory: File) : IStorage {
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

    override fun create(store: Store) {
        (0 until masterIndexFile.validIndexCount()).forEach {
            store.addIndex(it)
            cacheIndexFile(it)
        }
        logger.debug { "Loaded ${indexFiles.size} indices." }
    }

    private fun cacheIndexFile(id: Int): IndexFile {
        val cachedIndexFile = indexFiles.find { it.id() == id }

        if (cachedIndexFile == null) {
            val file = File("$directory/${FileConstants.MAIN_FILE_IDX}${id}")

            if (file.exists().not()) {
                throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_IDX} in directory $directory")
            }

            val indexFile = IndexFile(id, file)
            indexFiles.add(indexFile)
            return indexFile
        }

        return cachedIndexFile
    }

    override fun close() {
        masterIndexFile.close()
        datFile.close()
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}