package com.runetopic.cache.store.fs

import com.runetopic.cache.ReferenceTable
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