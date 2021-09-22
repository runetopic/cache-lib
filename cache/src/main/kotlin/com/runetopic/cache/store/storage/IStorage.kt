package com.runetopic.cache.store.storage

import com.runetopic.cache.Js5File
import com.runetopic.cache.Js5Group
import com.runetopic.cache.Js5Index
import com.runetopic.cache.store.Store
import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IStorage: Closeable, Flushable {
    fun init(store: Store)
    fun loadIndex(indexId: Int): Js5Index
    fun loadGroup(index: Js5Index, groupName: String): Js5Group?
    fun loadGroup(index: Js5Index, groupId: Int): Js5Group?
    fun loadFile(index: Js5Index, groupId: Int, fileId: Int): Js5File
    fun loadReferenceTable(index: Js5Index, groupId: Int): ByteArray
    fun loadMasterReferenceTable(groupId: Int): ByteArray
    fun loadReferenceTable(index: Js5Index, groupName: String): ByteArray
}