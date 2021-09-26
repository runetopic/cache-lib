package com.runetopic.cache.store

import com.runetopic.cache.crypto.Whirlpool
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.Js5File
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
        indexes.forEach { i -> require(index.getId() != i.getId()) { "Index with Id={${index.getId()}} already exists." } }
        this.indexes.add(index)
    }

    fun index(indexId: Int): Js5Index = this.indexes.find { it.getId() == indexId }!!
    fun group(index: Js5Index, groupName: String): Js5Group? = storage.loadGroup(index, groupName)
    fun group(indexId: Int, groupName: String): Js5Group? = storage.loadGroup(index(indexId), groupName)
    fun group(index: Js5Index, groupId: Int): Js5Group? = storage.loadGroup(index, groupId)
    fun group(indexId: Int, groupId: Int): Js5Group? = storage.loadGroup(index(indexId), groupId)
    fun file(index: Js5Index, groupId: Int, fileId: Int): Js5File = storage.loadFile(index, groupId, fileId)
    fun file(indexId: Int, groupId: Int, fileId: Int): Js5File = storage.loadFile(index(indexId), groupId, fileId)

    fun indexReferenceTableSize(indexId: Int): Int {
        var size = 0
        index(indexId).use { index ->
            index.getGroups().forEach {
                size += storage.loadReferenceTable(index, it.value.getId()).size
            }
        }
        return size
    }

    fun groupReferenceTableSize(indexId: Int, groupName: String): Int {
        val referenceTable = storage.loadReferenceTable(index(indexId), groupName)
        return if (referenceTable.isEmpty()) 0 else referenceTable.size - 2
    }

    fun groupReferenceTableSize(indexId: Int, groupId: Int): Int {
        val referenceTable = storage.loadReferenceTable(index(indexId), groupId)
        return if (referenceTable.isEmpty()) 0 else referenceTable.size - 2
    }

    fun groupReferenceTable(indexId: Int, groupId: Int): ByteArray {
        if (indexId == Constants.MASTER_INDEX_ID) return storage.loadMasterReferenceTable(groupId)
        return storage.loadReferenceTable(index(indexId), groupId)
    }

    fun checksumsWithoutRSA(): ByteArray {
        val header = ByteBuffer.allocate(indexes.size * 8)
        indexes.forEach {
            header.putInt(it.getCRC())
            header.putInt(it.getRevision())
        }
        return header.array()
    }

    fun checksumsWithRSA(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val header = ByteBuffer.allocate(indexes.size * 72 + 6)
        header.position(5)
        header.put(indexes.size.toByte())
        indexes.forEach {
            header.putInt(it.getCRC())
            header.putInt(it.getRevision())
            header.put(it.getWhirlpool())
        }
        val headerPosition = header.position()
        val headerArray = header.array()

        val whirlpool = ByteBuffer.allocate(Whirlpool.DIGESTBYTES + 1)
        whirlpool.put(1)
        whirlpool.put(Whirlpool.digest(headerArray, 5, headerPosition - 5))
        val rsa = BigInteger(whirlpool.array()).modPow(exponent, modulus).toByteArray()

        val checksums = ByteBuffer.allocate(headerPosition.plus(rsa.size))
        checksums.put(0)
        checksums.putInt((headerPosition.plus(rsa.size)) - 5)
        checksums.put(headerArray, 5, headerPosition - 5)
        checksums.put(rsa)
        return checksums.array()
    }

    override fun close() {
        storage.close()
    }
}