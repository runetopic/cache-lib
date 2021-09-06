package com.xlite.cache.file

import com.xlite.cache.ReferenceTable
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