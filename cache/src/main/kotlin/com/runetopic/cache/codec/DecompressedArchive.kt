package com.runetopic.cache.codec

import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class DecompressedArchive(
    val buffer: ByteBuffer,
    val compression: Int,
    val revision: Int,
    val crc: Int
)
