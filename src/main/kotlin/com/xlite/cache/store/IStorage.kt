package com.xlite.cache.store

import com.xlite.cache.Archive
import com.xlite.cache.Index
import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IStorage: Closeable, Flushable {
    fun init(store: Store)
    fun loadIndex(id: Int): Index
    fun readArchive(archive: Archive): ByteArray
    fun readFile(id: Int, archive: Archive, data: ByteArray): ByteArray
}