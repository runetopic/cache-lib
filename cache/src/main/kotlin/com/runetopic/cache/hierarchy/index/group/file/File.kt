package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
data class File(
    val id: Int,
    val nameHash: Int,
    val data: ByteArray
) : Comparable<File> {
    override fun compareTo(other: File): Int = this.id.compareTo(other.id)
}
