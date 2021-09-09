package com.xlite.cache.store.fs

import com.xlite.cache.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IIdxFile: Closeable {
    fun loadReferenceTable(fileId: Int): ReferenceTable
    fun validIndexCount(): Int
    fun id(): Int
}