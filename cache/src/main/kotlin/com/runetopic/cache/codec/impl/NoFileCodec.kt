package com.runetopic.cache.codec.impl

import com.runetopic.cache.codec.IFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class NoFileCodec : IFileCodec {
    override fun compress(data: ByteArray, keys: IntArray): ByteArray = throw NotImplementedError("No codec provided.")
    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray = throw NotImplementedError("No codec provided.")
}
