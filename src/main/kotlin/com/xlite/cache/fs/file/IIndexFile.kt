package com.xlite.cache.fs.file

import com.xlite.cache.fs.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IIndexFile: Closeable {
    fun loadReferenceTable(id: Int): ReferenceTable
    fun validIndexCount(): Int
    fun id(): Int
}