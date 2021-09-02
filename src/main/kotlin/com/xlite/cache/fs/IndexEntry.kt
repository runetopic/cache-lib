package com.xlite.cache.fs

import com.xlite.cache.Archive

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class IndexEntry(
    val id: Int,
    private val protocol: Int = 6,
    private val revision: Int = -1,
    private val crc: Int = -1,
    private val compression: Int = -1,
) {
    private val archives: ArrayList<Archive> = arrayListOf()
}