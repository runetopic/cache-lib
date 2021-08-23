package com.xlite.cache.fs.loader

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.fs.file.ReferenceTable
import com.xlite.cache.main
import com.xlite.cache.service.ICacheService

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class CacheLoader(private val cacheService: ICacheService): ICacheLoader {
    private val mainIndex = cacheService.getMainIndex()
    private val data = cacheService.getData()

    override fun load() {
        logger.debug { "Loaded ${cacheService.getMainIndex().length()} indices." }
    }

    @Synchronized
    override fun readIndex(id: Int): ByteArray {
        val table = mainIndex.read(id)
        return data.read(mainIndex.indexId(), table)
    }

    override fun close() {
        cacheService.close()
    }

    private companion object {
        private val logger = InlineLogger()
    }
}