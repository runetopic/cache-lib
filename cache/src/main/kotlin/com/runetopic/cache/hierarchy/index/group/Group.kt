package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.File

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
    val keys: IntArray,
    private val files: MutableMap<Int, File>,
    val data: ByteArray
): Comparable<Group> {

    @JvmName("getFiles")
    fun files(): Collection<File> = files.values

    @JvmName("getFileIds")
    fun fileIds(): Collection<Int> = files.keys

    @JvmName("getFile")
    fun file(fileId: Int): File = files[fileId] ?: File.DEFAULT

    internal fun putFile(id: Int, file: File) {
        files[id] = file
    }

    override fun compareTo(other: Group): Int = this.id.compareTo(other.id)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (id != other.id) return false
        if (nameHash != other.nameHash) return false
        if (crc != other.crc) return false
        if (!whirlpool.contentEquals(other.whirlpool)) return false
        if (revision != other.revision) return false
        if (!keys.contentEquals(other.keys)) return false
        if (files != other.files) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nameHash
        result = 31 * result + crc
        result = 31 * result + whirlpool.contentHashCode()
        result = 31 * result + revision
        result = 31 * result + keys.contentHashCode()
        result = 31 * result + files.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    internal companion object {
        val DEFAULT = Group(-1, -1, 0, byteArrayOf(), 0, intArrayOf(), mutableMapOf(), byteArrayOf())
    }
}
