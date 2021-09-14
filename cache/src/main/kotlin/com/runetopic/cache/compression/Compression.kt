package com.runetopic.cache.compression

import com.runetopic.cache.Container
import com.runetopic.cache.compression.CompressionType.*
import com.runetopic.cache.exception.CompressionException
import com.runetopic.cache.extension.decipherXTEA
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.remainingBytes
import java.nio.ByteBuffer
import java.util.zip.CRC32

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
object Compression {

    fun decompress(data: ByteArray, keys: Array<Int>): Container {
        val buffer = ByteBuffer.wrap(data)

        val compression = buffer.get().toInt() and 0xFF
        val length = buffer.int

        if (length < 0 || length > 2000000) {
            throw CompressionException("Compression issue. Length=[$length]")
        }

        val crc32 = CRC32()
        crc32.update(data, 0, 5)

        return when (val type = compressionType(compression)) {
            BadCompression -> throw CompressionException("Compression type not found with a compression opcode of $compression.")
            is NoCompression -> {
                val encrypted = ByteArray(length)
                buffer.get(encrypted, 0, length)
                crc32.update(encrypted, 0, length)
                val decrypted = encrypted.decipherXTEA(keys)

                val revision = -1 /*buffer.short.toInt() and 0xFFFF*/

                Container(decrypted, compression, revision, crc32.value.toInt())
            }
            GZipCompression, BZipCompression-> {
                val encryptedData = ByteArray(length + 4)
                buffer.get(encryptedData)
                crc32.update(encryptedData, 0, encryptedData.size)
                val decryptedData = encryptedData.decipherXTEA(keys)

                var revision = -1

                if (buffer.remaining() >= 2) {
                    revision = buffer.readUnsignedShort()
                }

                val byteBuffer = ByteBuffer.wrap(decryptedData)
                val decompressedLength = byteBuffer.int
                val decompressedData = type.codec.decompress(byteBuffer.remainingBytes(), length, keys)

                if (decompressedData.size != decompressedLength) {
                    throw CompressionException("Compression size mismatch.")
                }

                Container(decompressedData, compression, revision, crc32.value.toInt())
            }
        }
    }

    private fun compressionType(compression: Int): CompressionType {
        return when (compression) {
            0 -> NoCompression
            1 -> BZipCompression
            2 -> GZipCompression
            else -> BadCompression
        }
    }
}