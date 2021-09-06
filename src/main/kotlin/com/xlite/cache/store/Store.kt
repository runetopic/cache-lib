package com.xlite.cache.store

import com.xlite.cache.Index
import com.xlite.cache.store.impl.DiskStorage
import java.io.Closeable
import java.io.File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Store: Closeable {
    private var storage: IStorage
    private val indices: ArrayList<Index> = arrayListOf()

    constructor(directory: File) {
        this.storage = DiskStorage(directory)
        this.storage.init(this)
    }

    constructor(storage: IStorage) {
        this.storage = storage
        this.storage.init(this)
    }

    fun addIndex(index: Index) {
        indices.forEach { i -> require(index.id != i.id) { "Index with Id={${index.id}} already exists." } }
        this.indices.add(index)
    }

    override fun close() {
        storage.close()
    }
}