package com.runetopic.cache.hierarchy.index.group.file

import com.runetopic.cache.codec.decompress
import java.nio.ByteBuffer
import java.util.zip.ZipException

/**
 * @author Jordan Abraham
 */
interface File: Comparable<File> {
    fun getId(): Int
    fun getNameHash(): Int
    fun getData(): ByteArray

    override fun compareTo(other: File): Int {
        return getId().compareTo(other.getId())
    }
}

fun groupFiles(
    fileIds: Array<IntArray>,
    fileNameHashes: Array<IntArray>,
    groupReferenceTableData: ByteArray,
    count: Int,
    groupId: Int
): Map<Int, File> {
    if (groupReferenceTableData.isEmpty()) return hashMapOf(Pair(0, Js5File.DEFAULT))

    val src: ByteArray = try {
        groupReferenceTableData.decompress()
    } catch (exception: ZipException) {
        groupReferenceTableData
    }

    if (count == 1) {
        return hashMapOf(Pair(0, Js5File(fileIds[groupId][0], fileNameHashes[groupId][0], src)))
    }

    var position = src.size
    val chunks = src[--position].toInt() and 0xFF
    position -= chunks * (count * 4)
    val buffer = ByteBuffer.wrap(src)
    buffer.position(position)
    val filesSizes = IntArray(count)
    (0 until chunks).forEach { _ ->
        var read = 0
        (0 until count).forEach {
            read += buffer.int
            filesSizes[it] += read
        }
    }
    val filesDatas = Array(count) { byteArrayOf() }
    (0 until count).forEach {
        filesDatas[it] = ByteArray(filesSizes[it])
        filesSizes[it] = 0
    }
    buffer.position(position)
    var offset = 0
    (0 until chunks).forEach { _ ->
        var read = 0
        (0 until count).forEach {
            read += buffer.int
            System.arraycopy(src, offset, filesDatas[it], filesSizes[it], read)
            offset += read
            filesSizes[it] += read
        }
    }

    val files = hashMapOf<Int, Js5File>()
    (0 until count).forEach {
        files[it] = Js5File(fileIds[groupId][it], fileNameHashes[groupId][it], filesDatas[it])
    }
    return files
}