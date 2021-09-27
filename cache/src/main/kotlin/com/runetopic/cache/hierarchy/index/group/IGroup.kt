package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.IFile

/**
 * @author Jordan Abraham
 */
interface IGroup: Comparable<IGroup> {
    fun getId(): Int
    fun getNameHash(): Int
    fun getCRC(): Int
    fun getWhirlpool(): ByteArray
    fun getRevision(): Int
    fun getKeys(): IntArray
    fun getFiles(): Map<Int, IFile>
    fun getData(): ByteArray
    fun getFile(fileId: Int): IFile

    override fun compareTo(other: IGroup): Int {
        return getId().compareTo(other.getId())
    }
}