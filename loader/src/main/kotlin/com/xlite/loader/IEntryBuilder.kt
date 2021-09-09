package com.xlite.loader

import com.xlite.cache.store.Store
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal interface IEntryBuilder<T : IEntryType> {
    fun build(store: Store)
    fun read(buffer: ByteBuffer, type: T): T
}