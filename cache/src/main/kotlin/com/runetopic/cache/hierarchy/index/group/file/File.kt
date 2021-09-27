package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Jordan Abraham
 */
interface File: Comparable<File> {
    fun getId(): Int
    fun getNameHash(): Int
    fun getData(): ByteArray

    override fun compareTo(other: File): Int {
        return getId().compareTo(other.getId())
    }
}