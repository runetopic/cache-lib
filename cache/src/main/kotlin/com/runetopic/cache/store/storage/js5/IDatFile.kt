package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.hierarchy.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IDatFile: Closeable {
    fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray
}