package com.xlite.cache

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.config.CacheConfiguration
import com.xlite.cache.extension.inject
import com.xlite.cache.fs.loader.CacheLoader
import com.xlite.cache.service.ICacheService
import com.xlite.cache.service.impl.CacheServiceRS2
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.math.log

val applicationModule = module {
    single { CacheConfiguration.properties.getProperty("cache.location") }
    single<ICacheService> { CacheServiceRS2(get()) }
}

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
fun main() {
    startKoin { modules(applicationModule) }
    val serviceRS2 by inject<ICacheService>()
    val logger = InlineLogger()
    val loader = CacheLoader(serviceRS2)
    val index = loader.readIndex(5)
    logger.debug { "Valid Archive Count ${index.validArchiveCount}" }
    logger.debug { "Protocol ${index.protocol}" }
    logger.debug { "Revision ${index.revision}" }

    index.archives.forEach {
        logger.debug { "ArchiveId: ${it.id} ${it.files}" }
    }

    logger.debug { "Is Named ${index.isNamed}" }
}