package com.xlite.cache.store.fs

import com.xlite.cache.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IDatFile: Closeable {
    fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray
}