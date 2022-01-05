package com.runetopic.cache.store.storage.js5.io.idx

import com.runetopic.cache.hierarchy.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IdxFileCodec : Closeable {
    fun decode(id: Int): ReferenceTable
    fun encode(data: ByteArray)
    fun validIndexCount(): Int
    fun id(): Int
}