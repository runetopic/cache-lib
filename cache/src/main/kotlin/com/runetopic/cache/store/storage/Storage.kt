package com.runetopic.cache.store.storage

import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Js5Store

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface Storage {
    fun init(store: Js5Store)
    fun open(indexId: Int, store: Js5Store)
    fun loadReferenceTable(index: Index, groupId: Int): ByteArray
    fun loadMasterReferenceTable(groupId: Int): ByteArray
    fun loadReferenceTable(index: Index, groupName: String): ByteArray
}
