package com.xlite.cache.fs.compression

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IFileCodec {
    fun compress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray
    fun decompress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray
}