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

    fun checksumsWithoutRSA(): ByteArray = ByteBuffer.allocate(indexes.size * 8).apply {
        indexes.forEach {
            putInt(it?.crc ?: -1)
            putInt(it?.revision ?: -1)
        }
    }.array()

    fun checksumsWithRSA(exponent: BigInteger, modulus: BigInteger): ByteArray {
        val header = ByteBuffer.allocate(indexes.size * 72 + 6).apply {
            position(5)
            put(indexes.size.toByte())
            indexes.forEach {
                putInt(it?.crc ?: -1)
                putInt(it?.revision ?: -1)
                put(it?.whirlpool ?: ByteArray(64))
            }
        }.array()

        val whirlpool = ByteBuffer.allocate(64 + 1).apply {
            put(1)
            put(header.copyInto(ByteArray(header.size - 5), 0, 5, header.size).toWhirlpool())
        }

        val rsa = BigInteger(whirlpool.array()).modPow(exponent, modulus).toByteArray()
        return ByteBuffer.allocate(header.size + rsa.size).apply {
            put(0)
            putInt((header.size + rsa.size) - 5)
            put(header, 5, header.size - 5)
            put(rsa)
        }.array()
    }

    fun validIndexCount(): Int = indexes.size
}
