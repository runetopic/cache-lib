package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.File

/**
 * @author Jordan Abraham
 */
interface Group: Comparable<Group> {
    fun getId(): Int
    fun getNameHash(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getRevision(): Int
    fun getKeys(): IntArray
    fun getFiles(): Collection<File>
    fun getData(): ByteArray
    fun getFile(fileId: Int): File

    override fun compareTo(other: Group): Int {
        return getId().compareTo(other.getId())
    }
}