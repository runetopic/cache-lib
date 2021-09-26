package com.runetopic.cache.compression

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Container(
    val data: ByteArray,
    val compression: Int,
    val revision: Int,
    val crc: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Container

        if (!data.contentEquals(other.data)) return false
        if (compression != other.compression) return false
        if (revision != other.revision) return false
        if (crc != other.crc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + compression
        result = 31 * result + revision
        result = 31 * result + crc
        return result
    }
}
