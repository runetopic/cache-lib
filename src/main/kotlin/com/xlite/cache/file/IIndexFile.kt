package com.xlite.cache.file

import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IIndexFile: Closeable {
    fun length(): Int
}