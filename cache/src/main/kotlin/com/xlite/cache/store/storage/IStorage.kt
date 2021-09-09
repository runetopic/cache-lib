package com.xlite.cache.store.storage

import com.xlite.cache.Js5File
import com.xlite.cache.Js5FileEntry
import com.xlite.cache.Js5Group
import com.xlite.cache.store.Store
import java.io.Closeable
import java.io.Flushable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IStorage: Closeable, Flushable {
    fun init(store: Store)
    fun loadGroup(id: Int): Js5Group
    fun loadFile(group: Js5Group, fileName: String): Js5File?
    fun loadFile(group: Js5Group, fileId: Int): Js5File?
    fun loadEntry(group: Js5Group, fileId: Int, entryId: Int): Js5FileEntry
}