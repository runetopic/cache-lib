package com.runetopic.cache.store

import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.storage.js5.Js5DiskStorage
import com.runetopic.cryptography.toWhirlpool
import java.io.Closeable
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
class Js5Store(
    path: Path,
    parallel: Boolean = false,
    decompressionIndexExclusions: IntArray = intArrayOf()
) : Closeable {
    private var storage = Js5DiskStorage(path, parallel, decompressionIndexExclusions)
    private val indexes = CopyOnWriteArrayList<Index>()

    init {
        storage.init(this)
        indexes.sortWith(compareBy { it.id })
    }

    internal fun addIndex(index: Index) {
        indexes.forEach { i -> require(index.id != i.id) { "Index with Id={${index.id}} already exists." } }
        indexes.add(index)
    }

    fun index(indexId: Int): Index = indexes.find { it.id == indexId }!!

    fun indexReferenceTableSize(indexId: Int): Int {
        var size = 0
        index(indexId).use { index ->
            index.groups().forEach { size += storage.loadReferenceTable(index, it.id).size }
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
            header.putInt(it.crc)
            header.putInt(it.revision)
        }
        return header.array()
    }

    fun checksumsWithRSA(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val header = ByteBuffer.allocate(indexes.size * 72 + 6)
        header.position(5)
        header.put(indexes.size.toByte())
        indexes.forEach {
            header.putInt(it.crc)
            header.putInt(it.revision)
            header.put(it.whirlpool)
        }
        val headerPosition = header.position()
        val headerArray = header.array()

        val whirlpool = ByteBuffer.allocate(64 + 1)
        whirlpool.put(1)
        whirlpool.put(headerArray.copyInto(ByteArray(headerPosition - 5), 0, 5, headerPosition).toWhirlpool())

        val rsa = BigInteger(whirlpool.array()).modPow(exponent, modulus).toByteArray()
        val checksums = ByteBuffer.allocate(headerPosition + rsa.size)
        checksums.put(0)
        checksums.putInt((headerPosition + rsa.size) - 5)
        checksums.put(headerArray, 5, headerPosition - 5)
        checksums.put(rsa)
        return checksums.array()
    }

    fun validIndexCount() = indexes.size

    override fun close() = storage.close()
}
