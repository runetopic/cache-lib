package com.runetopic.cache.store.storage.js5.io.dat

/**
 * @author Jordan Abraham
 */
internal interface DatSectorCodec<T> {
    fun decode(): T
    fun encode(override: T): ByteArray
}
