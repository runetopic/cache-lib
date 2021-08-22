package com.xlite.cache.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IFileIndex {
    fun decode(): ByteArray
    fun length(): Int
}