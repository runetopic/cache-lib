package com.xlite.cache.fs.loader

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheLoader: AutoCloseable {
    fun load()
    fun readIndex(id: Int): ByteArray
}