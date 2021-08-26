package com.xlite.cache.fs

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Index(
    val id: Int,
    val protocol: Int,
    val revision: Int,
    val isNamed: Boolean,
    val archives: List<Archive>
)