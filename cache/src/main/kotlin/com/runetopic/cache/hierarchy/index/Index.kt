package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.hierarchy.index.group.Group

/**
 * @author Jordan Abraham
 */
interface Index: Comparable<Index> {
    fun getId(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getCompression(): Int
    fun getProtocol(): Int
    fun getRevision(): Int
    fun getIsNamed(): Boolean
    fun getGroups(): Collection<Group>
    fun getGroup(groupId: Int): Group
    fun getGroup(groupName: String): Group
    fun expand(): Int

    fun use(block: (Index) -> Unit) = block.invoke(this)

    override fun compareTo(other: Index): Int {
        return getId().compareTo(other.getId())
    }
}