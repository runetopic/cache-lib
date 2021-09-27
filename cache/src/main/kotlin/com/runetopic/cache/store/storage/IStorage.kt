package com.runetopic.cache.store.storage

import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Js5Store
import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IStorage: Closeable, Flushable {
    fun init(store: Js5Store)
    fun loadIndex(table: ReferenceTable, indexId: Int, whirlpool: ByteArray, referenceTable: ByteArray): Index
    fun loadReferenceTable(index: Index, groupId: Int): ByteArray
    fun loadMasterReferenceTable(groupId: Int): ByteArray
    fun loadReferenceTable(index: Index, groupName: String): ByteArray
}