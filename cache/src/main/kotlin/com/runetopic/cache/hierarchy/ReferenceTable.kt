package com.runetopic.cache.hierarchy

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal data class ReferenceTable(
    val id: Int,
    val sector: Int,
    val length: Int
) {
    fun exists(): Boolean = (length != 0 && sector != 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReferenceTable

        if (id != other.id) return false
        if (sector != other.sector) return false
        if (length != other.length) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + sector
        result = 31 * result + length
        return result
    }
}