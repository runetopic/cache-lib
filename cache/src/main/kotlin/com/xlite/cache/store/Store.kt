package com.xlite.cache.store

import com.xlite.cache.Js5FileEntry
import com.xlite.cache.Js5File
import com.xlite.cache.Js5Group
import com.xlite.cache.store.storage.impl.DiskStorage
import com.xlite.cache.store.storage.IStorage
import java.io.Closeable
import java.io.File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Store: Closeable {
    private var storage: IStorage
    private val groups: ArrayList<Js5Group> = arrayListOf()

    constructor(directory: File) {
        this.storage = DiskStorage(directory)
        this.storage.init(this)
    }

    constructor(storage: IStorage) {
        this.storage = storage
        this.storage.init(this)
    }

    fun addGroup(group: Js5Group) {
        groups.forEach { i -> require(group.id != i.id) { "Group with Id={${group.id}} already exists." } }
        this.groups.add(group)
    }

    fun group(id: Int): Js5Group = this.groups[id]
    fun file(group: Js5Group, fileName: String): Js5File = storage.loadFile(group, fileName)
    fun file(group: Js5Group, fileId: Int): Js5File = storage.loadFile(group, fileId)
    fun file(groupId: Int, fileId: Int): Js5File = storage.loadFile(group(groupId), fileId)
    fun entry(group: Js5Group, fileId: Int, entryId: Int): Js5FileEntry = storage.loadEntry(group, fileId, entryId)
    fun entry(groupId: Int, fileId: Int, entryId: Int): Js5FileEntry? = storage.loadEntry(group(groupId), fileId, entryId)

    override fun close() {
        storage.close()
    }
}