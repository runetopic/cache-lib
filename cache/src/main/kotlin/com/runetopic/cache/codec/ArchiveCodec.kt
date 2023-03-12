package com.runetopic.cache.codec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface ArchiveCodec {
    fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray
}
