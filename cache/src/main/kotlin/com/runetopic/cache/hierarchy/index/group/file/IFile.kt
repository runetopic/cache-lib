package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Jordan Abraham
 */
interface IFile: Comparable<IFile> {
    fun getId(): Int
    fun getGroupId(): Int
    fun getNameHash(): Int
    fun getData(): ByteArray
    fun setData(data: ByteArray)
}