package com.xlite.cache.config

import java.util.*


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
object CacheConfiguration {
    val properties = Properties()

    init {
        this::class.java.getResourceAsStream("/cache.properties").use { properties.load(it) }
    }
}