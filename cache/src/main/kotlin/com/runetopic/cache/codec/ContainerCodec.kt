package com.runetopic.cache.codec

import com.runetopic.cache.exception.CompressionException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.remainingBytes
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cryptography.fromXTEA
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.zip.CRC32

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal object ContainerCodec {

    fun compress(
        compression: Int,
        revision: Int,
        data: ByteArray,
        keys: IntArray = intArrayOf()
    ): ByteBuffer {
        val codec = compressionCodec(compression)
        val compressed = if (codec != NoCodec) codec.codec.compress(data, keys) else data
        val buffer = ByteBuffer.allocate((if (codec != NoCodec) 9 else 5) + compressed.size/* + if (revision == -1) 0 else 2*/)
        buffer.put(compressionType(codec).toByte())
        buffer.putInt(compressed.size)
        if (codec != NoCodec) {
            buffer.putInt(data.size)
        }
        buffer.put(compressed)
        if (keys.isNotEmpty()) {
            // TODO xtea
        }
        if (revision != -1) {
            // buffer.putShort(revision.toShort())
        }
        return buffer
    }

    fun decompress(
        data: ByteArray,
        keys: IntArray = intArrayOf()
    ): Container {
        val buffer = data.toByteBuffer()
        val compression = buffer.readUnsignedByte()
        val length = buffer.int

        if (length < 0 || length > 2000000) {
            throw CompressionException("Compression issue. Length=[$length]")
        }

        val crc32 = CRC32()
        crc32.update(data, 0, 5)

        return when (val type = compressionCodec(compression)) {
            BadCodec -> throw CompressionException("Compression type not found with a compression opcode of $compression.")
            is NoCodec -> {
                val encrypted = ByteArray(length)
                buffer.get(encrypted, 0, length)
                crc32.update(encrypted, 0, length)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                val revision = -1 /*buffer.short.toInt() and 0xFFFF*/

                Container(decrypted, compression, revision, crc32.value.toInt())
            }
            GZipCodec, BZipCodec -> {
                val encrypted = ByteArray(length + 4)
                buffer.get(encrypted)
                crc32.update(encrypted, 0, encrypted.size)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                var revision = -1

                if (buffer.remaining() >= 2) {
                    revision = buffer.readUnsignedShort()
                }

                val byteBuffer = decrypted.toByteBuffer()
                val decompressedLength = byteBuffer.int
                val decompressedData = type.codec.decompress(byteBuffer.remainingBytes(), length, keys)

                if (decompressedData.size != decompressedLength) {
                    throw CompressionException("Compression size mismatch.")
                }

                Container(decompressedData, compression, revision, crc32.value.toInt())
            }
        }
    }

    private fun compressionType(codecType: CodecType): Int {
        return when (codecType) {
            is NoCodec -> 0
            is BZipCodec -> 1
            is GZipCodec -> 2
            else -> throw Exception("Bad compression type.")
        }
    }

    private fun compressionCodec(compression: Int): CodecType {
        return when (compression) {
            0 -> NoCodec
            1 -> BZipCodec
            2 -> GZipCodec
            else -> BadCodec
        }
    }
}
