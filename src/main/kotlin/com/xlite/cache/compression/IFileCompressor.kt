package com.xlite.cache.compression

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IFileCompressor {
    fun compressFile(data: ByteArray): ByteArray
}