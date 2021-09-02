package com.xlite.cache.fs.store

import com.xlite.cache.fs.IndexEntry
import com.xlite.cache.fs.store.impl.DiskStorage
import java.io.Closeable
import java.io.File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Store: Closeable {
    private var storage: IStorage
    private val indices: ArrayList<IndexEntry> = arrayListOf()

    constructor(directory: File) {
        this.storage = DiskStorage(directory)
        this.storage.create(this)
    }

    constructor(storage: IStorage) {
        this.storage = storage
        this.storage.create(this)
    }

    fun addIndex(id: Int) {
        indices.forEach { i -> require(id != i.id) { "Index with Id={$id} already exists." } }
        this.indices.add(IndexEntry(id))
    }

    override fun close() {
        storage.close()
    }
}