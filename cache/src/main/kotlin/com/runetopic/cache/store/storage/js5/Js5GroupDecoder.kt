package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.extension.decompress
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cache.hierarchy.index.group.Group
import com.runetopic.cache.hierarchy.index.group.file.File
import java.lang.System.arraycopy
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal fun Group.decodeJs5Group(keys: IntArray = intArrayOf()): Array<File> {
    if (data.isEmpty() || fileCount <= 1) return arrayOf(File(fileIds[0], fileNameHashes[0], data))
    val decompressed = data.decompress(keys).buffer
    var position = decompressed.capacity()
    val chunks = decompressed[--position].toInt() and 0xFF
    position -= chunks * (fileCount * 4)
    decompressed.position(position)
    val fileChunks = decompressed.decodeFileChunks(chunks, fileCount)
    val fileSegments = decompressed.decodeFileSegments(fileCount, fileChunks)
    decompressed.position(position)
    decompressed.decodeFiles(decompressed.array(), fileCount, chunks, fileChunks, fileSegments)
    return Array(fileCount) { File(fileIds[it], fileNameHashes[it], fileSegments[it]) }
}

private tailrec fun ByteBuffer.decodeFileChunks(
    chunks: Int,
    count: Int,
    curr: Int = 0,
    filesSizes: IntArray = IntArray(count)
): IntArray {
    if (curr == chunks) return filesSizes
    decodeFileChunkSizes(count, filesSizes)
    return decodeFileChunks(chunks, count, curr + 1, filesSizes)
}

private tailrec fun ByteBuffer.decodeFileChunkSizes(
    count: Int,
    filesSizes: IntArray,
    offset: Int = 0,
    curr: Int = 0
) {
    if (curr == count) return
    filesSizes[curr] = offset + int
    return decodeFileChunkSizes(count, filesSizes, filesSizes[curr], curr + 1)
}

private tailrec fun ByteBuffer.decodeFileSegments(
    count: Int,
    fileChunks: IntArray,
    curr: Int = 0,
    segments: Array<ByteArray> = Array(count) { byteArrayOf() }
): Array<ByteArray> {
    if (curr == count) return segments
    segments[curr] = ByteArray(fileChunks[curr])
    fileChunks[curr] = 0
    return decodeFileSegments(count, fileChunks, curr + 1, segments)
}

private tailrec fun ByteBuffer.decodeFiles(
    data: ByteArray,
    count: Int,
    chunks: Int,
    fileChunks: IntArray,
    fileSegments: Array<ByteArray>,
    offset: Int = 0,
    curr: Int = 0
) {
    if (curr == chunks) return
    return decodeFiles(data, count, chunks, fileChunks, fileSegments, offset + decodeFileChunkSegmentSize(data, count, chunks, fileChunks, fileSegments, offset, curr), curr + 1)
}

private tailrec fun ByteBuffer.decodeFileChunkSegmentSize(
    data: ByteArray,
    count: Int,
    chunks: Int,
    fileChunks: IntArray,
    fileSegments: Array<ByteArray>,
    offset: Int,
    read: Int,
    curr: Int = 0
): Int {
    if (curr == count) return offset
    val size = read + int
    arraycopy(data, offset, fileSegments[curr], fileChunks[curr], size)
    fileChunks[curr] += size
    return decodeFileChunkSegmentSize(data, count, chunks, fileChunks, fileSegments, offset + size, size, curr + 1)
}