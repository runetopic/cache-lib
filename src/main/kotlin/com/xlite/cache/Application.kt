package com.xlite.cache

import com.xlite.cache.config.CacheConfiguration

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
fun main() {
    Cache().load(CacheConfiguration.properties.getProperty("cache.location"))
}