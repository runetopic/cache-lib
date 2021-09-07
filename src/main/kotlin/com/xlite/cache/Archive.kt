package com.xlite.cache

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.compression.Compression
import com.xlite.cache.file.impl.FileEntry
import java.lang.NullPointerException
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class Archive(
    val id: Int,
    val indexId: Int,
    val nameHash: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val revision: Int,
    val keys: IntArray,
    val files: Array<FileEntry>,
): Comparable<Archive> {

    override fun compareTo(other: Archive): Int {
        return id.compareTo(other.id)
    }

    val logger = InlineLogger()

    fun decodeFileEntry(id: Int, data: ByteArray): ByteArray {
        val fileEntry = files.filterIndexed { index, _ ->  files[index].id == id}.first()

        val decompressed = Compression.decompress(data, emptyArray())

        val count = files.size
        if (count == 1) {
            return data
        }

        return fileEntry.data ?: let {
            logger.debug { "NOT LOADED" }
            var size = decompressed.data.size
            val chunks: Int = decompressed.data[--size].toInt() and 0xFF
            size -= chunks * (count * 4)
            val buffer = ByteBuffer.wrap(decompressed.data)
            buffer.position(size)
            val entriesSizes = IntArray(count)
            (0 until chunks).forEach { _ ->
                var read = 0
                (0 until count).forEach {
                    read += buffer.int
                    entriesSizes[it] += read
                }
            }
            val entries = Array(count) { byteArrayOf() }
            (0 until count).forEach {
                entries[it] = ByteArray(entriesSizes[it])
                entriesSizes[it] = 0
            }
            buffer.position(size)
            var offset = 0
            (0 until chunks).forEach { _ ->
                var read = 0
                (0 until count).forEach {
                    read += buffer.int
                    System.arraycopy(decompressed.data, offset, entries[it], entriesSizes[it], read)
                    offset += read
                    entriesSizes[it] += read
                }
            }

            files.indices.forEach {
                val file = files[it]
                file.data = entries[it]
            }

            fileEntry.data ?: throw NullPointerException("GDSFSFSDF")
        }
    }
}
