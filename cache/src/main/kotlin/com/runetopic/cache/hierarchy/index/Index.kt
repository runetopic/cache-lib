package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.extension.nameHash
import com.runetopic.cache.hierarchy.index.group.Group

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
data class Index(
    val id: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val compression: Int,
    val protocol: Int,
    val revision: Int,
    val isNamed: Boolean,
    val isUsingWhirlpool: Boolean,
    private val groups: Map<Int, Group>
) : Comparable<Index> {

    @JvmName("getGroups")
    fun groups(): Collection<Group> = groups.values

    @JvmName("getGroupIds")
    fun groupIds(): Collection<Int> = groups.keys

    @JvmName("getGroup")
    fun group(groupId: Int): Group = groups[groupId] ?: Group.DEFAULT

    @JvmName("getGroup")
    fun group(groupName: String): Group = groups.values.find { it.nameHash == groupName.nameHash() } ?: Group.DEFAULT

    fun expand(): Int = groups.values.last().files().size + (groups.values.last().id shl 8)

    override fun compareTo(other: Index): Int = this.id.compareTo(other.id)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Index

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

    internal companion object {
        fun default(indexId: Int): Index = Index(indexId, 0, ByteArray(64), -1, -1, 0, false, false, hashMapOf())
    }
}
