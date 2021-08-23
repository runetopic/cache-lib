package com.xlite.cache.loader

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheLoader: AutoCloseable {
    fun load()
}