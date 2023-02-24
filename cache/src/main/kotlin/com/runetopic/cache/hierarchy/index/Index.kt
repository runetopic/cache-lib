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
    fun group(groupId: Int): Group? = groups.firstOrNull { it.id == groupId }

    @JvmName("getGroup")
    fun group(groupName: String): Group? = groups.firstOrNull { it.nameHash == groupName.hashed() }

    fun expand(): Int = groups.last().files().size + (groups.last().id shl 8)

    override fun compareTo(other: Index): Int = this.id.compareTo(other.id)
}
