package com.xlite.cache

import com.xlite.cache.fs.file.impl.IndexFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Index(
    val indexFile: IndexFile,
    val id: Int,
    val protocol: Int,
    val revision: Int,
    val isNamed: Boolean,
    val archives: List<Archive>
)