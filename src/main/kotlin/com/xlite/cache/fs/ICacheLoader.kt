package com.xlite.cache.fs

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheLoader: AutoCloseable {
    fun load()
}