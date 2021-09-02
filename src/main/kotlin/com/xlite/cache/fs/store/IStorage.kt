package com.xlite.cache.fs.store

import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IStorage: Closeable, Flushable {
    fun create(store: Store)
}