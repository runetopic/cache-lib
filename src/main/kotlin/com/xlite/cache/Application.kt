package com.xlite.cache

import com.xlite.cache.config.CacheConfiguration
import com.xlite.cache.extension.inject
import com.xlite.cache.fs.loader.CacheLoader
import com.xlite.cache.service.ICacheService
import com.xlite.cache.service.impl.CacheServiceRS2
import org.koin.core.context.startKoin
import org.koin.dsl.module

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
    val loader = CacheLoader(serviceRS2)
    val indexData = loader.readIndex(5)
    indexData.forEach { println(it) }
}