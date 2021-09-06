package com.xlite.cache.file.impl

import com.xlite.cache.Archive
import com.xlite.cache.compression.Compression
import com.xlite.cache.file.IFileEntry
import java.lang.System.arraycopy
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class FileEntry(
    val id: Int = -1,
    var nameHash: Int = -1,
) : IFileEntry {

    override fun decode(id: Int, data: ByteArray, archive: Archive): ByteArray {
        val decompressed = Compression.decompress(data, emptyArray())

        val count = archive.files.size
        if (count == 1) {
            return data
        }

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
                arraycopy(decompressed.data, offset, entries[it], entriesSizes[it], read)
                offset += read
                entriesSizes[it] += read
            }
        }
        return entries[id]
    }

    override fun encode(): ByteArray {
        TODO("Not yet implemented")
    }
}