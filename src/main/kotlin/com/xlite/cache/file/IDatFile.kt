package com.xlite.cache.file

import com.xlite.cache.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IDatFile: Closeable {
    fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray
}