package com.xlite.cache.fs

import com.xlite.cache.fs.file.CacheFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class Archive(
    val id: Int,
    var nameHash: Int = 0,
    var crc: Int = 0,
    var revision: Int = 0,
    var keys: IntArray? = null
 ): Comparable<Archive> {

    var files: List<CacheFile> = ArrayList<CacheFile>()

    override fun compareTo(other: Archive): Int {
        return id.compareTo(other.id)
    }
}
