package com.xlite.cache.fs.file

import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IIndexFile: Closeable {
    fun read(id: Int): ReferenceTable
    fun length(): Int
    fun indexId(): Int
}