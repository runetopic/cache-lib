package com.runetopic.cache.store

import com.runetopic.cache.crypto.Whirlpool

import com.runetopic.cache.Js5Group
import com.runetopic.cache.Js5File
import com.runetopic.cache.Js5Index
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
    private val indexes: ArrayList<Js5Index> = arrayListOf()

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

    fun addIndex(index: Js5Index) {
        indexes.forEach { i -> require(index.id != i.id) { "Index with Id={${index.id}} already exists." } }
        this.indexes.add(index)
    }

    fun index(indexId: Int): Js5Index = this.indexes.find { it.id == indexId }!!
    fun group(index: Js5Index, groupName: String): Js5Group? = storage.loadGroup(index, groupName)
    fun group(indexId: Int, groupName: String): Js5Group? = storage.loadGroup(index(indexId), groupName)
    fun group(index: Js5Index, groupId: Int): Js5Group? = storage.loadGroup(index, groupId)
    fun group(indexId: Int, groupId: Int): Js5Group? = storage.loadGroup(index(indexId), groupId)
    fun file(index: Js5Index, groupId: Int, fileId: Int): Js5File = storage.loadFile(index, groupId, fileId)
    fun file(indexId: Int, groupId: Int, fileId: Int): Js5File = storage.loadFile(index(indexId), groupId, fileId)

    fun fetchIndexReferenceTableSize(indexId: Int): Int {
        var size = 0
        index(indexId).use { index ->
            index.groups.forEach {
                size += storage.loadReferenceTable(index, it.value.groupId).size
            }
        }
        return size
    }

    fun fetchGroupReferenceTableSize(indexId: Int, groupName: String): Int {
        val referenceTable = storage.loadReferenceTable(index(indexId), groupName)
        return if (referenceTable.isEmpty()) 0 else referenceTable.size
    }

    fun fetchGroupReferenceTableSize(indexId: Int, groupId: Int): Int {
        val referenceTable = storage.loadReferenceTable(index(indexId), groupId)
        return if (referenceTable.isEmpty()) 0 else referenceTable.size
    }

    fun fetchGroupReferenceTable(indexId: Int, groupId: Int): ByteArray {
        return storage.loadReferenceTable(index(indexId), groupId)
    }

    fun generateUpdateKeys(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val buffer = ByteBuffer.allocate((6 + indexes.size) * 72)
        buffer
            .position(5)
            .put(indexes.size.toByte())
        indexes.forEach {
            buffer
                .putInt(it.crc)
                .putInt(it.revision)
                .put(it.whirlpool)
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