package com.xlite.cache.fs.compression.impl

import com.xlite.cache.fs.compression.IFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class UhandledFileCodec: IFileCodec {
    override fun compress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        throw NotImplementedError("Unhandled codec provided.")
    }

    override fun decompress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        throw NotImplementedError("Unhandled codec provided.")
    }
}