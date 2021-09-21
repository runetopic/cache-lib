package com.runetopic.cache

import com.runetopic.cache.extension.nameHash

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Js5Index(
    internal val id: Int,
    val crc: Int,
    val whirlpool: ByteArray? = null,
    internal val compression: Int,
    internal val protocol: Int,
    internal val revision: Int,
    internal val isNamed: Boolean,
    internal val files: Map<Int, Js5Group>
) {
    internal fun getGroup(fileId: Int): Js5Group? = files[fileId]
    internal fun getGroup(name: String): Js5Group? = files.values.find { it.nameHash == name.nameHash() }

    fun use(block: (Js5Index) -> Unit) = block.invoke(this)

    fun entries(fileId: Int) = files[fileId]?.files!!

    fun expandedCapacity(): Int = files.values.last().files.size + (files.values.last().groupId shl 8)

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
        if (files != other.files) return false

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
        result = 31 * result + files.hashCode()
        return result
    }
}