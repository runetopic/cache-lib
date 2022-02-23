package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.hierarchy.ReferenceTable
import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IIdxFile : Closeable {
    fun loadReferenceTable(id: Int): ReferenceTable
    fun validIndexCount(): Int
    fun id(): Int
}
