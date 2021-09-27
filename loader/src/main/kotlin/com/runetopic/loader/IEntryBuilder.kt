package com.runetopic.loader

import com.runetopic.cache.store.storage.js5.Js5Store
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal interface IEntryBuilder<T : IEntryType> {
    fun build(store: Js5Store)
    fun read(buffer: ByteBuffer, type: T): T
}