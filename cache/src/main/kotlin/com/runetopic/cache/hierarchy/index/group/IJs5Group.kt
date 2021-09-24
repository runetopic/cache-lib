package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.Js5File

/**
 * @author Jordan Abraham
 */
interface IJs5Group: Comparable<IJs5Group> {
    fun getId(): Int
    fun getNameHash(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getRevision(): Int
    fun getKeys(): IntArray
    fun getFiles(): Array<Js5File>
    fun getData(): ByteArray

    override fun compareTo(other: IJs5Group): Int {
        return getId().compareTo(other.getId())
    }
}