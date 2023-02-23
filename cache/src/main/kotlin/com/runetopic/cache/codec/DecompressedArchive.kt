package com.runetopic.cache.codec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class DecompressedArchive(
    val data: ByteArray,
    val compression: Int,
    val revision: Int,
    val crc: Int
)
