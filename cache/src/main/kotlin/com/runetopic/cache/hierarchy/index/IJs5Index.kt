package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.Js5File

/**
 * @author Jordan Abraham
 */
internal interface IJs5Index {
    fun getId(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getCompression(): Int
    fun getProtocol(): Int
    fun getRevision(): Int
    fun getIsNamed(): Boolean
    fun getGroups(): Map<Int, Js5Group>
    fun getFiles(groupId: Int): Array<Js5File>
    fun expand(): Int
}