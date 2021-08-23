package com.xlite.cache

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.file.impl.DataFile
import com.xlite.cache.file.impl.IndexFile
import java.io.File
import java.io.FileNotFoundException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Cache(private val directory: String) {
    private val dat2File = getDat2File()
    private val mainIndex = getMainIndex()

    fun load() {
        cacheIndices(mainIndex)
        logger.debug { "Found a total of ${mainIndex.length()} indices." }
    }

    private fun cacheIndices(mainIndex: IndexFile) {
        for (i in 0 until mainIndex.length()) {
            val file = File("${directory}${MAIN_FILE_IDX}$i")
            val index = IndexFile(file)
            cachedIndices.add(index)
        }
    }

    private fun getMainIndex(): IndexFile {
        val file = File("${directory}${MAIN_FILE_255}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_255 in directory $directory")
        return IndexFile(file)
    }

    private fun getDat2File(): DataFile {
        val file = File("${directory}${MAIN_FILE_DAT}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_DAT in directory $directory")
        return DataFile(file)
    }

    private companion object {
        private const val PREFIX = "main_file_cache"
        private const val MAIN_FILE_IDX = "${PREFIX}.idx"
        private const val MAIN_FILE_DAT = "${PREFIX}.dat2"
        private const val MAIN_FILE_255 = "${PREFIX}.idx255"

        private val logger = InlineLogger()
        private val cachedIndices = arrayListOf<IndexFile>()
    }
}