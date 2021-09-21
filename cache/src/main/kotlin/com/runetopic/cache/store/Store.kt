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

    fun index(id: Int): Js5Index = this.indexes.find { it.id == id }!!
    fun group(group: Js5Index, fileName: String): Js5Group? = storage.loadGroup(group, fileName)
    fun group(groupId: Int, fileName: String): Js5Group? = storage.loadGroup(index(groupId), fileName)
    fun group(group: Js5Index, fileId: Int): Js5Group? = storage.loadGroup(group, fileId)
    fun group(groupId: Int, fileId: Int): Js5Group? = storage.loadGroup(index(groupId), fileId)
    fun file(group: Js5Index, fileId: Int, entryId: Int): Js5File = storage.loadFile(group, fileId, entryId)
    fun file(groupId: Int, fileId: Int, entryId: Int): Js5File = storage.loadFile(index(groupId), fileId, entryId)

    fun fetchIndexReferenceTableSize(groupId: Int): Int {
        var total = 0
        index(groupId).use { group ->
            group.files.forEach {
                total += storage.loadReferenceTable(group, it.value.groupId).size
            }
        }
        return total
    }

    fun fetchGroupReferenceTableSize(groupId: Int, groupName: String): Int {
        val referenceTable = storage.loadReferenceTable(index(groupId), groupName)
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
        val emptyBuffer = ByteArray(Whirlpool.DIGESTBYTES)
        indexes.forEach {
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