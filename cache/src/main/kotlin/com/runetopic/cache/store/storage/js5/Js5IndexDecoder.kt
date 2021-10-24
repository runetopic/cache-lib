package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.codec.Container
import com.runetopic.cache.codec.decompress
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.*
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedIntShortSmart
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.hierarchy.index.group.Group
import com.runetopic.cache.hierarchy.index.group.file.File
import java.nio.ByteBuffer
import java.util.zip.ZipException

/**
 * @author Jordan Abraham
 */
internal fun decode(
    datFile: IDatFile,
    idxFile: IIdxFile,
    whirlpool: ByteArray,
    decompressed: Container
): Index {
    val buffer = decompressed.data.toByteBuffer()
    val protocol = buffer.readUnsignedByte()
    val revision = when {
        protocol < 5 || protocol > 7 -> throw ProtocolException("Unhandled protocol $protocol")
        protocol >= 6 -> buffer.int
        else -> 0
    }
    val mask = buffer.readUnsignedByte()
    val count = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()

    val isNamed = (0x1 and mask) != 0
    val isUsingWhirlpool = (0x2 and mask) != 0

    val groupIds = decodeGroupIds(count, buffer, protocol)
    val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
    val groupNameHashes = decodeGroupNameHashes(maxGroupId, count, isNamed, groupIds, buffer)
    val groupCrcs = decodeGroupCrcs(maxGroupId, count, groupIds, buffer)
    val groupWhirlpools = decodeGroupWhirlpools(maxGroupId, isUsingWhirlpool, count, buffer, groupIds)
    val groupRevisions = decodeGroupRevisions(maxGroupId, count, groupIds, buffer)
    val groupFileIds = decodeGroupFileIds(maxGroupId, count, groupIds, buffer, protocol)
    val fileIds = decodeFileIds(maxGroupId, groupFileIds, count, groupIds, buffer, protocol)
    val fileNameHashes = decodeFileNameHashes(maxGroupId, groupFileIds, count, groupIds, buffer, isNamed)

    val groups = hashMapOf<Int, Group>()
    (0 until count).forEach {
        val groupReferenceTableData = datFile.readReferenceTable(idxFile.id(), idxFile.loadReferenceTable(it))
        val data = if (groupReferenceTableData.isEmpty()) byteArrayOf() else try {
            groupReferenceTableData.decompress()
        } catch (exception: ZipException) {
            groupReferenceTableData
        }

        val groupId = groupIds[it]
        groups[it] = (Group(
            groupId,
            groupNameHashes[groupId],
            groupCrcs[groupId],
            groupWhirlpools[groupId],
            groupRevisions[groupId],
            intArrayOf(),//TODO
            decodeFiles(fileIds, fileNameHashes, data, groupFileIds[it], it),
            data
        ))
    }
    return Index(idxFile.id(), decompressed.crc, whirlpool, decompressed.compression, protocol, revision, isNamed, isUsingWhirlpool, groups)
}

private fun decodeGroupIds(
    count: Int,
    buffer: ByteBuffer,
    protocol: Int
): IntArray {
    val groupIds = IntArray(count)
    var offset = 0
    (0 until count).forEach {
        groupIds[it] = if (protocol >= 7) { buffer.readUnsignedIntShortSmart() } else { buffer.readUnsignedShort() }
            .let { id -> offset += id; offset }
    }
    return groupIds
}

fun decodeGroupFileIds(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    buffer: ByteBuffer,
    protocol: Int
): IntArray {
    val groupFileIds = IntArray(maxGroupId)
    (0 until count).forEach {
        groupFileIds[groupIds[it]] = if (protocol >= 7) buffer.readUnsignedIntShortSmart() else buffer.readUnsignedShort()
    }
    return groupFileIds
}

fun decodeGroupRevisions(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    buffer: ByteBuffer
): IntArray {
    val revisions = IntArray(maxGroupId)
    (0 until count).forEach {
        revisions[groupIds[it]] = buffer.int
    }
    return revisions
}

fun decodeGroupWhirlpools(
    maxGroupId: Int,
    usesWhirlpool: Boolean,
    count: Int,
    buffer: ByteBuffer,
    groupIds: IntArray
): Array<ByteArray> {
    val whirlpools = Array(maxGroupId) { ByteArray(64) }
    if (usesWhirlpool.not()) return whirlpools

    (0 until count).forEach {
        val whirlpool = ByteArray(64)
        buffer.get(whirlpool)
        whirlpools[groupIds[it]] = whirlpool
    }
    return whirlpools
}

fun decodeGroupCrcs(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    buffer: ByteBuffer
): IntArray {
    val crcs = IntArray(maxGroupId)
    (0 until count).forEach {
        crcs[groupIds[it]] = buffer.int
    }
    return crcs
}

fun decodeGroupNameHashes(
    maxGroupId: Int,
    count: Int,
    isNamed: Boolean,
    groupIds: IntArray,
    buffer: ByteBuffer
): IntArray {
    val nameHashes = IntArray(maxGroupId) { -1 }
    if (isNamed.not()) return nameHashes

    (0 until count).forEach {
        nameHashes[groupIds[it]] = buffer.int
    }
    return nameHashes
}

private fun decodeFileIds(
    maxGroupId: Int,
    validFileIds: IntArray,
    count: Int,
    groupIds: IntArray,
    buffer: ByteBuffer,
    protocol: Int
): Array<IntArray> {
    val fileIds = Array(maxGroupId) { IntArray(validFileIds[it]) }
    (0 until count).forEach {
        val groupId = groupIds[it]
        var offset = 0
        (0 until validFileIds[groupId]).forEach { fileId ->
            if (protocol >= 7) { buffer.readUnsignedIntShortSmart() } else { buffer.readUnsignedShort() }
                .let { i -> offset += i; offset }
                .also { fileIds[groupId][fileId] = offset }
        }
    }
    return fileIds
}

private fun decodeFileNameHashes(
    maxGroupId: Int,
    validFileIds: IntArray,
    count: Int,
    groupIds: IntArray,
    buffer: ByteBuffer,
    isNamed: Boolean
): Array<IntArray> {
    val fileNameHashes = Array(maxGroupId) { IntArray(validFileIds[it]) }
    if (isNamed.not()) return fileNameHashes

    (0 until count).forEach {
        val groupId = groupIds[it]
        (0 until validFileIds[groupId]).forEach { fileId ->
            fileNameHashes[groupId][fileId] = buffer.int
        }
    }
    return fileNameHashes
}

internal fun decodeFiles(
    fileIds: Array<IntArray>,
    fileNameHashes: Array<IntArray>,
    data: ByteArray,
    count: Int,
    groupId: Int
): Map<Int, File> {
    if (data.isEmpty()) return hashMapOf(Pair(0, File.DEFAULT))
    if (count == 1) return hashMapOf(Pair(0, File(fileIds[groupId][0], fileNameHashes[groupId][0], data)))

    var position = data.size
    val chunks = data[--position].toInt() and 0xFF
    position -= chunks * (count * 4)
    val buffer = data.toByteBuffer()
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
            System.arraycopy(data, offset, filesDatas[it], filesSizes[it], read)
            offset += read
            filesSizes[it] += read
        }
    }

    val files = hashMapOf<Int, File>()
    (0 until count).forEach {
        files[it] = File(fileIds[groupId][it], fileNameHashes[groupId][it], filesDatas[it])
    }
    return files
}