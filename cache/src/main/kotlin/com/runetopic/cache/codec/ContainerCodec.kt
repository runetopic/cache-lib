package com.runetopic.cache.codec

import com.runetopic.cache.codec.CodecType.*
import com.runetopic.cache.exception.CompressionException
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.remainingBytes
import com.runetopic.cryptography.ext.fromXTEA
import java.nio.ByteBuffer
import java.util.zip.CRC32

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal object ContainerCodec {

    fun decompress(data: ByteArray, keys: IntArray = intArrayOf()): Container {
        val buffer = ByteBuffer.wrap(data)

        val compression = buffer.get().toInt() and 0xFF
        val length = buffer.int

        if (length < 0 || length > 2000000) {
            throw CompressionException("Compression issue. Length=[$length]")
        }

        val crc32 = CRC32()
        crc32.update(data, 0, 5)

        return when (val type = compressionType(compression)) {
            BadCodec -> throw CompressionException("Compression type not found with a compression opcode of $compression.")
            is NoCodec -> {
                val encrypted = ByteArray(length)
                buffer.get(encrypted, 0, length)
                crc32.update(encrypted, 0, length)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                val revision = -1 /*buffer.short.toInt() and 0xFFFF*/

                Container(decrypted, compression, revision, crc32.value.toInt())
            }
            GZipCodec, BZipCodec-> {
                val encrypted = ByteArray(length + 4)
                buffer.get(encrypted)
                crc32.update(encrypted, 0, encrypted.size)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                var revision = -1

                if (buffer.remaining() >= 2) {
                    revision = buffer.readUnsignedShort()
                }

                val byteBuffer = ByteBuffer.wrap(decrypted)
                val decompressedLength = byteBuffer.int
                val decompressedData = type.codec.decompress(byteBuffer.remainingBytes(), length, keys)

                if (decompressedData.size != decompressedLength) {
                    throw CompressionException("Compression size mismatch.")
                }

                Container(decompressedData, compression, revision, crc32.value.toInt())
            }
        }
    }

    private fun compressionType(compression: Int): CodecType {
        return when (compression) {
            0 -> NoCodec
            1 -> BZipCodec
            2 -> GZipCodec
            else -> BadCodec
        }
    }
}