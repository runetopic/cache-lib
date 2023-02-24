package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.File
import com.runetopic.cache.store.storage.js5.decodeJs5Group

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
data class Group(
    val id: Int,
    val nameHash: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val revision: Int,
    val fileCount: Int,
    val fileIds: IntArray,
    val fileNameHashes: IntArray,
    val data: ByteArray
) : Comparable<Group> {
    private lateinit var groupFiles: Array<File>

    @JvmName("getFiles")
    fun files(): Array<File> {
        if (::groupFiles.isInitialized) return groupFiles
        return decodeJs5Group().apply { groupFiles = Array(size) { this[it] } }
    }

    @JvmName("getFile")
    fun file(fileId: Int): File? {
        if (::groupFiles.isInitialized) return groupFiles.firstOrNull { it.id == fileId }
        return decodeJs5Group().apply { groupFiles = Array(size) { this[it] } }.firstOrNull { it.id == fileId }
    }

    @JvmName("getFiles")
    fun files(keys: IntArray): Array<File> {
        if (::groupFiles.isInitialized) return groupFiles
        return decodeJs5Group(keys).apply { groupFiles = Array(size) { this[it] } }
    }

    @JvmName("getFile")
    fun file(fileId: Int, keys: IntArray): File? {
        if (::groupFiles.isInitialized) return groupFiles.firstOrNull { it.id == fileId }
        return decodeJs5Group(keys).apply { groupFiles = Array(size) { this[it] } }.firstOrNull { it.id == fileId }
    }

    override fun compareTo(other: Group): Int = this.id.compareTo(other.id)
}
