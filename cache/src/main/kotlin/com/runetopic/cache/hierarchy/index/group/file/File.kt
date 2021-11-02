package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
data class File(
    val id: Int,
    val nameHash: Int,
    val data: ByteArray
): Comparable<File> {

    override fun compareTo(other: File): Int = this.id.compareTo(other.id)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (id != other.id) return false
        if (nameHash != other.nameHash) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nameHash
        result = 31 * result + data.contentHashCode()
        return result
    }

    internal companion object {
        val DEFAULT = File(-1, 0, byteArrayOf(0))
    }
}