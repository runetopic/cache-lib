package com.runetopic.loader

import com.runetopic.cache.store.Store
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal interface IEntryBuilder<T : IEntryType> {
    fun build(store: Store)
    fun read(buffer: ByteBuffer, type: T): T
}