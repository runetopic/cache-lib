package com.xlite.cache.fs

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Index(
    val protocol: Int,
    val revision: Int,
    val isNamed: Boolean,
    val validArchiveCount: Int,
    val archives: MutableList<Archive> = mutableListOf()
)