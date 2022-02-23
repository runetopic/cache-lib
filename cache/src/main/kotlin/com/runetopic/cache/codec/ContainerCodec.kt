package com.runetopic.cache.codec

import com.runetopic.cache.codec.impl.GZipCodec
import com.runetopic.cache.exception.CompressionException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.remainingBytes
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cryptography.fromXTEA
import java.util.zip.CRC32

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal object ContainerCodec {

    fun decompress(data: ByteArray, keys: IntArray = intArrayOf()): Container {
        val buffer = data.toByteBuffer()
        val compression = buffer.readUnsignedByte()
        val length = buffer.int

        if (length < 0 || length > 2000000) {
            throw CompressionException("Compression issue. Length=[$length]")
        }

        val crc32 = CRC32()
        crc32.update(data, 0, 5)

        return when (val type = compressionType(compression)) {
            CodecType.BadCodec -> throw CompressionException("Compression type not found with a compression opcode of $compression.")
            is CodecType.NoCodec -> {
                val encrypted = ByteArray(length)
                buffer.get(encrypted, 0, length)
                crc32.update(encrypted, 0, length)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                var revision = -1

                if (buffer.remaining() >= 2) {
                    revision = buffer.readUnsignedShort()
                    assert(revision != -1) { "Revision not properly decoded with no codec. Was -1" }
                }

                Container(decrypted, compression, revision, crc32.value.toInt())
            }
            CodecType.GZipCodec, CodecType.BZipCodec -> {
                val encrypted = ByteArray(length + 4)
                buffer.get(encrypted)
                crc32.update(encrypted, 0, encrypted.size)
                val decrypted = if (keys.isEmpty()) encrypted else encrypted.fromXTEA(32, keys)

                var revision = -1

                if (buffer.remaining() >= 2) {
                    revision = buffer.readUnsignedShort()
                    assert(revision != -1) { "Revision not properly decoded. Was -1" }
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

    private fun compressionType(compression: Int): CodecType {
        return when (compression) {
            0 -> CodecType.NoCodec
            1 -> CodecType.BZipCodec
            2 -> CodecType.GZipCodec
            else -> CodecType.BadCodec
        }
    }
}
