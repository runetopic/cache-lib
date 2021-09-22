package com.runetopic.cache.store.fs

import com.runetopic.cache.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IDatFile: Closeable {
    fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray
}