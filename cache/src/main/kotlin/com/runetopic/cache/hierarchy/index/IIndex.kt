package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.hierarchy.index.group.IGroup

/**
 * @author Jordan Abraham
 */
interface IIndex: Comparable<IIndex> {
    fun getId(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getCompression(): Int
    fun getProtocol(): Int
    fun getRevision(): Int
    fun getIsNamed(): Boolean
    fun getGroups(): Collection<IGroup>
    fun getGroup(groupId: Int): IGroup
    fun getGroup(groupName: String): IGroup
    fun expand(): Int

    fun use(block: (IIndex) -> Unit) = block.invoke(this)

    override fun compareTo(other: IIndex): Int {
        return getId().compareTo(other.getId())
    }
}