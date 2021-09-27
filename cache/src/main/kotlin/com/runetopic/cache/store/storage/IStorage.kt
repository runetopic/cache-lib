package com.runetopic.cache.store.storage

import com.runetopic.cache.hierarchy.index.IIndex
import com.runetopic.cache.store.Store
import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IStorage: Closeable, Flushable {
    fun init(store: Store)
    fun loadIndex(indexId: Int): IIndex
    fun loadReferenceTable(index: IIndex, groupId: Int): ByteArray
    fun loadMasterReferenceTable(groupId: Int): ByteArray
    fun loadReferenceTable(index: IIndex, groupName: String): ByteArray
}