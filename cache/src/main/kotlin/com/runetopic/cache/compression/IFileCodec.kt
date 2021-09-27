package com.runetopic.cache.compression

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface IFileCodec {
    fun compress(data: ByteArray, length: Int, keys: IntArray): ByteArray
    fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray
}