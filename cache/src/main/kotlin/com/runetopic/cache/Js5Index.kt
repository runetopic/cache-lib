package com.runetopic.cache

import com.runetopic.cache.extension.nameHash

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Js5Index(
    internal val id: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    internal val compression: Int,
    internal val protocol: Int,
    internal val revision: Int,
    internal val isNamed: Boolean,
    internal val groups: Map<Int, Js5Group>
) {
    internal fun getGroup(groupId: Int): Js5Group? = groups[groupId]
    internal fun getGroup(groupName: String): Js5Group? = groups.values.find { it.nameHash == groupName.nameHash() }

    fun use(block: (Js5Index) -> Unit) = block.invoke(this)
    fun files(groupId: Int) = groups[groupId]?.files!!
    fun expand(): Int = groups.values.last().files.size + (groups.values.last().groupId shl 8)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Js5Index

        if (id != other.id) return false
        if (crc != other.crc) return false
        if (!whirlpool.contentEquals(other.whirlpool)) return false
        if (compression != other.compression) return false
        if (protocol != other.protocol) return false
        if (revision != other.revision) return false
        if (isNamed != other.isNamed) return false
        if (groups != other.groups) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + crc
        result = 31 * result + whirlpool.contentHashCode()
        result = 31 * result + compression
        result = 31 * result + protocol
        result = 31 * result + revision
        result = 31 * result + isNamed.hashCode()
        result = 31 * result + groups.hashCode()
        return result
    }
}