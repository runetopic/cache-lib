package com.runetopic.cache

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Js5FileEntry(
    val fileId: Int = -1,
    val entryId: Int = -1,
    internal var nameHash: Int = -1,
    var data: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Js5FileEntry

        if (entryId != other.entryId) return false
        if (nameHash != other.nameHash) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entryId
        result = 31 * result + nameHash
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}