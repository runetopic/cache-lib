package com.runetopic.cache.store.storage.js5.io.dat

import com.runetopic.cache.hierarchy.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface DatFileCodec : Closeable {
    fun decode(id: Int, referenceTable: ReferenceTable): ByteArray
    fun encode(data: ByteArray)
}
