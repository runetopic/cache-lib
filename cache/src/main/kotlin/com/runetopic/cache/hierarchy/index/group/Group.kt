package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.extension.decompress
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
    private lateinit var fileArray: Array<File>

    @JvmName("getFiles")
    fun files(): Array<File> {
        if (::fileArray.isInitialized) return fileArray

        return decodeJs5Group(
            fileIds,
            fileNameHashes,
            fileCount,
            data.decompress().data
        ).apply { fileArray = Array(size) { this[it] } }
    }

    @JvmName("getFile")
    fun file(fileId: Int): File {
        if (::fileArray.isInitialized) return fileArray.firstOrNull { it.id == fileId } ?: File.DEFAULT

        return decodeJs5Group(
            fileIds,
            fileNameHashes,
            fileCount,
            data.decompress().data
        ).apply { fileArray = Array(size) { this[it] } }.firstOrNull { it.id == fileId } ?: File.DEFAULT
    }

    @JvmName("getFiles")
    fun files(keys: IntArray): Array<File> {
        if (::fileArray.isInitialized) return fileArray

        return decodeJs5Group(
            fileIds,
            fileNameHashes,
            fileCount,
            data.decompress(keys).data
        ).apply { fileArray = Array(size) { this[it] } }
    }

    @JvmName("getFile")
    fun file(fileId: Int, keys: IntArray): File {
        if (::fileArray.isInitialized) return fileArray.firstOrNull { it.id == fileId } ?: File.DEFAULT

        return decodeJs5Group(
            fileIds,
            fileNameHashes,
            fileCount,
            data.decompress(keys).data
        ).apply { fileArray = Array(size) { this[it] } }.firstOrNull { it.id == fileId } ?: File.DEFAULT
    }

    override fun compareTo(other: Group): Int = this.id.compareTo(other.id)

    internal companion object {
        val DEFAULT = Group(
            id = -1,
            nameHash = -1,
            crc = -1,
            whirlpool = byteArrayOf(),
            revision = -1,
            fileCount = -1,
            fileIds = intArrayOf(),
            fileNameHashes = intArrayOf(),
            data = byteArrayOf()
        )
    }
}
