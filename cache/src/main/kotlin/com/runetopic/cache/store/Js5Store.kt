package com.runetopic.cache.store

import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.storage.js5.Js5DiskStorage
import com.runetopic.cryptography.toWhirlpool
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
    parallel: Boolean = false
) {
    private var storage = Js5DiskStorage(path, parallel)
    private val indexes = arrayOfNulls<Index>(storage.validIndexCount())

    init {
        storage.init(this)
    }

    internal fun addIndex(index: Index) {
        indexes[index.id] = index
    }

    fun index(indexId: Int): Index = indexes[indexId]!!

    fun indexReferenceTableSize(indexId: Int): Int = index(indexId).let { it.groups().fold(0) { size, group -> size + storage.loadReferenceTable(it, group.id).size } }

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
            header.putInt(it?.crc ?: -1)
            header.putInt(it?.revision ?: -1)
        }
        return header.array()
    }

    fun checksumsWithRSA(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val header = ByteBuffer.allocate(indexes.size * 72 + 6)
        header.position(5)
        header.put(indexes.size.toByte())
        indexes.forEach {
            header.putInt(it?.crc ?: -1)
            header.putInt(it?.revision ?: -1)
            header.put(it?.whirlpool)
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

    fun validIndexCount(): Int = indexes.size
}
