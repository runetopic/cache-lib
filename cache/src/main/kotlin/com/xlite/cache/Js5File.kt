package com.xlite.cache

import com.xlite.cache.compression.Compression
import com.xlite.cache.exception.ArchiveDataException
import com.xlite.cache.store.fs.IDatFile
import com.xlite.cache.store.fs.IIdxFile
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class Js5File(
    val id: Int,
    val indexId: Int,
    val nameHash: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val revision: Int,
    val keys: IntArray,
    val entries: Array<Js5FileEntry>,
    var isLoaded: Boolean = false,
    var data: ByteArray? = null
): Comparable<Js5File> {

    override fun compareTo(other: Js5File): Int {
        return id.compareTo(other.id)
    }

    internal fun load(datFile: IDatFile, idxFile: IIdxFile): ByteArray {
        if (this.isLoaded) {
            return this.data!!
        }

        val referenceTable = idxFile.loadReferenceTable(id)
        this.data = datFile.readReferenceTable(indexId, referenceTable)

        if (this.data != null) {
            this.isLoaded = true
            return this.data ?: throw ArchiveDataException("Archive data could not be loaded.")
        }

        return byteArrayOf()
    }

    internal fun loadFileEntriesData(fileId: Int, js5File: Js5File): ByteArray {
        val fileEntry = entries.find { it.id == fileId } ?: return byteArrayOf()

        val decompressed = Compression.decompress(js5File.data!!, emptyArray())

        val count = entries.size

        if (count == 1) {
            return decompressed.data
        }

        return fileEntry.data ?: let {
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

            this.entries.indices.forEach { this.entries[it].data = entries[it] }
            fileEntry.data ?: throw ArchiveDataException("Could not load file entries from archive.")
        }
    }
}
