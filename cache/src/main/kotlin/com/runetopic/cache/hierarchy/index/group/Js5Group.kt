package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.compression.Compression
import com.runetopic.cache.exception.FileDataException
import com.runetopic.cache.hierarchy.index.group.file.Js5File
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Js5Group(
    private val id: Int,
    private val nameHash: Int,
    private val crc: Int,
    private val whirlpool: ByteArray,
    private val revision: Int,
    private val keys: IntArray,
    private val files: Array<Js5File>,
    private val data: ByteArray
): IGroup {
    override fun getId(): Int = id
    override fun getNameHash(): Int = nameHash
    override fun getCRC(): Int = crc
    override fun getWhirlpool(): ByteArray = whirlpool
    override fun getRevision(): Int = revision
    override fun getKeys(): IntArray = keys
    override fun getFiles(): Array<Js5File> = files
    override fun getData(): ByteArray = data

    internal fun loadFiles(file: Js5File) {
        //TODO: Make this better.
        val decompressed = Compression.decompress(data, emptyArray())
        val count = files.size
        if (count == 1) {
            file.data = decompressed.data
            return
        }
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
