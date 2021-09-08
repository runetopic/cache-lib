package com.xlite.loader

import com.xlite.cache.store.Store
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal interface IEntryBuilder<T : IEntryType> {
    fun build(store: Store)
    fun read(buf: ByteBuffer, type: T): T
}