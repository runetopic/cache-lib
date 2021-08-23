package com.xlite.cache.fs.file

import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IDataFile: Closeable {
    fun read(id: Int, referenceTable: ReferenceTable): ByteArray
}