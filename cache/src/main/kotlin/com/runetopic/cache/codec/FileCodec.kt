package com.runetopic.cache.codec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface FileCodec {
    fun compress(data: ByteArray, keys: IntArray): ByteArray
    fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray
}
