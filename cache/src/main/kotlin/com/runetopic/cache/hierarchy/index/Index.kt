package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.extension.hashed
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
    private val groups: Array<Group>
) : Comparable<Index> {

    @JvmName("getGroups")
    fun groups(): Array<Group> = groups

    @JvmName("getGroup")
    fun group(groupId: Int): Group = groups[groupId]

    @JvmName("getGroup")
    fun group(groupName: String): Group = groups.find { it.nameHash == groupName.hashed() } ?: Group.DEFAULT

    fun expand(): Int = groups.last().files().size + (groups.last().id shl 8)

    override fun compareTo(other: Index): Int = this.id.compareTo(other.id)

    internal companion object {
        fun default(indexId: Int): Index = Index(indexId, 0, ByteArray(64), -1, -1, 0, false, arrayOf())
    }
}
