package com.runetopic.cache.extension

import com.runetopic.cache.codec.CodecType
import com.runetopic.cache.codec.DecompressedArchive
import com.runetopic.cache.exception.CompressionException
import com.runetopic.cryptography.fromXTEA
import java.nio.ByteBuffer
import java.util.zip.CRC32

/**
 * @author Jordan Abraham
 */
fun ByteArray.decompress(): DecompressedArchive = decompress(intArrayOf())

fun ByteArray.decompress(keys: IntArray): DecompressedArchive {
    val buffer = toByteBuffer()
    val compression = buffer.readUnsignedByte()
    val length = buffer.int

    if (length < 0 || length > 2000000) {
        throw CompressionException("Compression issue. Length should be between 0 and 2000000 bytes. Length=[$length]")
    }

    val crc32 = CRC32().apply { update(this@decompress, 0, 5) }

    return when (compression) {
        0 -> {
            val encrypted = ByteArray(length).apply { buffer.get(this) }
            crc32.update(encrypted, 0, length)
            val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)
            val revision = if (buffer.remaining() >= 2) buffer.readUnsignedShort().also { assert(it != -1) { "Revision not properly decoded with no codec. Was -1" } } else -1
            DecompressedArchive(decrypted, compression, revision, crc32.value.toInt())
        }
        1, 2 -> {
            val encrypted = ByteArray(length + 4).apply { buffer.get(this) }
            crc32.update(encrypted, 0, encrypted.size)
            val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)
            val revision = if (buffer.remaining() >= 2) buffer.readUnsignedShort().also { assert(it != -1) { "Revision not properly decoded with no codec. Was -1" } } else -1

            val byteBuffer = decrypted.toByteBuffer()
            val decryptedLength = byteBuffer.int
            val decryptedData = with(if (compression == 1) CodecType.bzip else CodecType.gzip) {
                decompress(byteBuffer.remainingBytes(), length, keys)
            }

            if (decryptedData.size != decryptedLength) {
                throw CompressionException("Compression size mismatch.")
            }

            DecompressedArchive(decryptedData, compression, revision, crc32.value.toInt())
        }
        else -> throw CompressionException("Compression type not found with a compression type of $compression.")
    }
}

internal fun ByteArray.toByteBuffer(): ByteBuffer = ByteBuffer.wrap(this)
