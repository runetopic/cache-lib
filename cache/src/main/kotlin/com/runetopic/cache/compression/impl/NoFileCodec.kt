package com.runetopic.cache.compression.impl

import com.runetopic.cache.compression.IFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class NoFileCodec: IFileCodec {
    override fun compress(data: ByteArray, length: Int, keys: IntArray): ByteArray {
        throw NotImplementedError("No codec provided.")
    }

    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray {
        throw NotImplementedError("No codec provided.")
    }
}