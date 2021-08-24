package com.xlite.cache.fs.file

import com.xlite.cache.fs.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IDataFile: Closeable {
    fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray
}