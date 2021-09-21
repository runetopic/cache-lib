package com.runetopic.cache

import com.runetopic.cache.compression.Compression
import com.runetopic.cache.exception.FileDataException
import com.runetopic.cache.store.fs.IDatFile
import com.runetopic.cache.store.fs.IIdxFile
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class Js5Group(
    internal val indexId: Int,
    internal val groupId: Int,
    internal val nameHash: Int,
    internal val crc: Int,
    internal val whirlpool: ByteArray,
    internal val revision: Int,
    internal val keys: IntArray,
    internal val files: Array<Js5File>,
    private var isLoaded: Boolean = false,
    var data: ByteArray? = null
): Comparable<Js5Group> {

    override fun compareTo(other: Js5Group): Int {
        return groupId.compareTo(other.groupId)
    }

    internal fun loadGroup(datFile: IDatFile, idxFile: IIdxFile, groupId: Int): ByteArray {
        if (isLoaded) {
            return data!!
        }

        data = datFile.readReferenceTable(indexId, idxFile.loadReferenceTable(groupId))

        if (data != null) {
            isLoaded = true
            return data ?: throw FileDataException("Group data could not be loaded.")
        }

        return byteArrayOf()
    }

    internal fun loadFiles(fileId: Int): ByteArray {
        val file = files.find { it.id == fileId } ?: return byteArrayOf()

        val decompressed = Compression.decompress(data!!, emptyArray())

        val count = files.size

        if (count == 1) {
            return decompressed.data
        }

        return file.data ?: let {
            var size = decompressed.data.size
            val chunks: Int = decompressed.data[--size].toInt() and 0xFF
            size -= chunks * (count * 4)
            val buffer = ByteBuffer.wrap(decompressed.data)
            buffer.position(size)
            val filesSizes = IntArray(count)
            (0 until chunks).forEach { _ ->
                var read = 0
                (0 until count).forEach {
                    read += buffer.int
                    filesSizes[it] += read
                }
            }
            val files = Array(count) { byteArrayOf() }
            (0 until count).forEach {
                files[it] = ByteArray(filesSizes[it])
                filesSizes[it] = 0
            }
            buffer.position(size)
            var offset = 0
            (0 until chunks).forEach { _ ->
                var read = 0
                (0 until count).forEach {
                    read += buffer.int
                    System.arraycopy(decompressed.data, offset, files[it], filesSizes[it], read)
                    offset += read
                    filesSizes[it] += read
                }
            }

            this.files.indices.forEach { this.files[it].data = files[it] }
            file.data ?: throw FileDataException("Could not load group files from archive.")
        }
    }
}
