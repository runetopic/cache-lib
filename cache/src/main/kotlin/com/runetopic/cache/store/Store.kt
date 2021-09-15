package com.runetopic.cache.store

import com.runetopic.cache.crypto.Whirlpool

import com.runetopic.cache.Js5File
import com.runetopic.cache.Js5FileEntry
import com.runetopic.cache.Js5Group
import com.runetopic.cache.store.storage.IStorage
import com.runetopic.cache.store.storage.impl.DiskStorage
import java.io.Closeable
import java.io.File
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Store(
    directory: File
) : Closeable {
    private var storage: IStorage = DiskStorage(directory)
    private val groups: ArrayList<Js5Group> = arrayListOf()

    init {
        this.storage.init(this)
    }

    /*constructor(directory: File) {
        this.storage = DiskStorage(directory)
        this.storage.init(this)
    }*/

    /*constructor(storage: IStorage) {
        this.storage = storage
        this.storage.init(this)
    }*/

    fun addGroup(group: Js5Group) {
        groups.forEach { i -> require(group.id != i.id) { "Group with Id={${group.id}} already exists." } }
        this.groups.add(group)
    }

    fun group(id: Int): Js5Group = this.groups[id]
    fun file(group: Js5Group, fileName: String): Js5File? = storage.loadFile(group, fileName)
    fun file(groupId: Int, fileName: String): Js5File? = storage.loadFile(group(groupId), fileName)
    fun file(group: Js5Group, fileId: Int): Js5File? = storage.loadFile(group, fileId)
    fun file(groupId: Int, fileId: Int): Js5File? = storage.loadFile(group(groupId), fileId)
    fun entry(group: Js5Group, fileId: Int, entryId: Int): Js5FileEntry = storage.loadEntry(group, fileId, entryId)
    fun entry(groupId: Int, fileId: Int, entryId: Int): Js5FileEntry = storage.loadEntry(group(groupId), fileId, entryId)

    fun generateUpdateKeys(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val buffer = ByteBuffer.allocate((6 + groups.size) * 72)
        buffer
            .position(5)
            .put(groups.size.toByte())
        val emptyBuffer = ByteArray(Whirlpool.DIGESTBYTES)
        groups.forEach {
            buffer
                .putInt(it.crc)
                .putInt(it.revision)
                .put(it.whirlpool ?: emptyBuffer)
        }
        val groupArray = buffer.array()
        val whirlpoolBuffer = ByteBuffer
            .allocate(Whirlpool.DIGESTBYTES + 1)
            .put(1)
            .put(Whirlpool.digest(groupArray, 5, groupArray.size - 5))
        buffer.put(BigInteger(whirlpoolBuffer.array()).modPow(exponent, modulus).toByteArray())
        val end = buffer.position() + 1
        buffer.position(0)
        buffer
            .put(0)
            .putInt(end - 5)
        buffer.position(end)
        return buffer.array()
    }

    override fun close() {
        storage.close()
    }
}