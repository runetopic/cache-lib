package com.xlite.cache

import com.xlite.cache.config.CacheConfiguration
import com.xlite.cache.loader.CacheLoader
import com.xlite.cache.service.impl.CacheServiceRS2

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
fun main() {
    val directory = CacheConfiguration.properties.getProperty("cache.location")
    val serviceRS2 = CacheServiceRS2(directory)
    CacheLoader(serviceRS2).load()
}