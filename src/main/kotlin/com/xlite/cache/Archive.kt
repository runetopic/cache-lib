package com.xlite.cache

import com.xlite.cache.file.impl.FileEntry

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class Archive(
    val id: Int,
    val indexId: Int,
    val nameHash: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val revision: Int,
    val keys: IntArray,
    val files: Array<FileEntry>,
): Comparable<Archive> {

    override fun compareTo(other: Archive): Int {
        return id.compareTo(other.id)
    }
}
