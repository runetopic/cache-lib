package com.runetopic.cache.store.storage.js5.io.dat

/**
 * @author Jordan Abraham
 */
internal interface IDatSector<T> {
    fun decode(): T
    fun encode(override: T): ByteArray
}
