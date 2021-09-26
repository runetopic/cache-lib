package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.hierarchy.index.group.IGroup
import com.runetopic.cache.hierarchy.index.group.file.Js5File

/**
 * @author Jordan Abraham
 */
internal interface IIndex: Comparable<IIndex> {
    fun getId(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getCompression(): Int
    fun getProtocol(): Int
    fun getRevision(): Int
    fun getIsNamed(): Boolean
    fun getGroups(): Map<Int, IGroup>
    fun getFiles(groupId: Int): Array<Js5File>
    fun expand(): Int

    override fun compareTo(other: IIndex): Int {
        return getId().compareTo(other.getId())
    }
}