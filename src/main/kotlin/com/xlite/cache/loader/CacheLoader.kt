package com.xlite.cache.loader

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.constant.FileConstants.MAIN_FILE_IDX
import com.xlite.cache.fs.file.impl.IndexFile
import com.xlite.cache.loader.impl.ICacheLoader
import com.xlite.cache.service.impl.CacheServiceRS2
import java.io.File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class CacheLoader(private val cacheServiceRS2: CacheServiceRS2): ICacheLoader {
    override fun load() {
        cacheIndices(cacheServiceRS2.getMainIndex())
        logger.debug { "Found a total of ${cacheServiceRS2.getMainIndex().length()} indices." }
    }

    override fun close() {
        cacheServiceRS2.close()
    }

    private fun cacheIndices(mainIndex: IndexFile) {
        for (i in 0 until mainIndex.length()) {
            val file = File("${mainIndex.file.path}${MAIN_FILE_IDX}$i")
            val index = IndexFile(file)
            cachedIndices.add(index)
        }
    }

    private companion object {
        private val logger = InlineLogger()
        private val cachedIndices = arrayListOf<IndexFile>()
    }
}