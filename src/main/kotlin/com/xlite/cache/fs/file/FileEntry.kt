package com.xlite.cache.fs.file

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.fs.Archive
import com.xlite.cache.fs.compression.Compression
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class FileEntry(
    val id: Int = 0, var nameHash: Int = 0,
) {

    private val logger = InlineLogger()

    fun decode(id: Int, data: ByteArray, archive: Archive): ByteArray {
        val container = Compression.decompress(data, emptyArray())

        val filesCount = archive.files.size
        if (filesCount == 1) {
            return data
        }

        var readPosition = container.data.size
        val amtOfLoops: Int = container.data[--readPosition].toInt() and 0xff
        readPosition -= amtOfLoops * (filesCount * 4)
        val stream = ByteBuffer.wrap(container.data)
        stream.position(readPosition)
        val filesSize = IntArray(filesCount)
        for (loop in 0 until amtOfLoops) {
            var offset = 0
            for (i in 0 until filesCount) {
                offset += stream.int
                filesSize[i] += offset
            }
        }
        val filesData = Array(filesCount) { byteArrayOf() }
        for (i in 0 until filesCount) {
            filesData[i] = ByteArray(filesSize[i])
            filesSize[i] = 0
        }
        stream.position(readPosition)
        var sourceOffset = 0
        for (loop in 0 until amtOfLoops) {
            var dataRead = 0
            for (i in 0 until filesCount) {
                dataRead += stream.int
                System.arraycopy(container.data, sourceOffset, filesData[i], filesSize[i], dataRead)
                sourceOffset += dataRead
                filesSize[i] += dataRead
            }
        }
        return filesData[id]
    }
}