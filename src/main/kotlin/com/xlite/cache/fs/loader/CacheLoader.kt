package com.xlite.cache.fs.loader

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.service.ICacheService

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class CacheLoader(private val cacheService: ICacheService): ICacheLoader {
    override fun load() {
        logger.debug { "Loaded ${cacheService.getMainIndex().length()} indices." }
    }

    override fun close() {
        cacheService.close()
    }

    private companion object {
        private val logger = InlineLogger()
    }
}