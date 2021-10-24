package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.extension.Variable
import com.runetopic.cache.extension.putIntShortSmart
import com.runetopic.cache.hierarchy.index.Index
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal fun encode(
    index: Index
): ByteBuffer {
    val header = ByteBuffer.allocate(if (index.protocol >= 7) 6 else 4)
    header.put(index.protocol.toByte())

    val mask = 0x0
    if (index.isNamed) mask or 0x1
    if (index.isUsingWhirlpool) mask or 0x2
    header.put(mask.toByte())

    val count = index.groups().size
    if (index.protocol >= 7) header.putIntShortSmart(count) else header.putShort(count.toShort())

    val stream = index.groups().stream()
    val groupIds = stream.map { it.id }.toList().toIntArray()
    val nameHashes = stream.map { it.nameHash }.toList().toIntArray()
    val crcs = stream.map { it.crc }.toList().toIntArray()
    val whirlpools = stream.map { it.whirlpool }.toList().toTypedArray()
    val revisions = stream.map { it.revision }.toList().toIntArray()

    val groupNameHashes = encodeGroupNameHashes(count, index.isNamed, groupIds, nameHashes)
    val groupCrcs = encodeGroupCrcs(count, groupIds, crcs)
    val groupWhirlpools = encodeGroupWhirlpools(count, groupIds, index.isUsingWhirlpool, whirlpools)
    val groupRevisions = encodeGroupRevisions(count, groupIds, revisions)

    val buffer = ByteBuffer.allocate(header.position()
            + groupNameHashes.position()
            + groupCrcs.position()
            + groupWhirlpools.position()
            + groupRevisions.position())

    buffer.put(header)
    //TODO Group ids
    buffer.put(groupNameHashes)
    buffer.put(groupCrcs)
    buffer.put(groupWhirlpools)
    buffer.put(groupRevisions)
    //TODO The rest
    return buffer
}

fun encodeGroupFileIds(
    count: Int,
    groupIds: IntArray,
    protocol: Int,
    groupFileIds: IntArray
): ByteBuffer {
    var capacity = 0
    (0 until count).forEach {
        capacity += Variable.asSizeBytes(protocol, groupFileIds[groupIds[it]])
    }
    val buffer = ByteBuffer.allocate(capacity)
    (0 until count).forEach {
        if (protocol >= 7) buffer.putIntShortSmart(groupFileIds[groupIds[it]]) else buffer.putShort(groupFileIds[groupIds[it]].toShort())
    }
    return buffer
}

fun encodeGroupRevisions(
    count: Int,
    groupIds: IntArray,
    revisions: IntArray
): ByteBuffer {
    val buffer = ByteBuffer.allocate(count * Int.SIZE_BYTES)
    (0 until count).forEach {
        buffer.putInt(revisions[groupIds[it]])
    }
    return buffer
}

fun encodeGroupWhirlpools(
    count: Int,
    groupIds: IntArray,
    usesWhirlpool: Boolean,
    whirlpools: Array<ByteArray>
): ByteBuffer {
    if ((usesWhirlpool.not())) return ByteBuffer.allocate(0)

    val buffer = ByteBuffer.allocate(count * 64)
    (0 until count).forEach {
        buffer.put(whirlpools[groupIds[it]])
    }
    return buffer
}

fun encodeGroupCrcs(
    count: Int,
    groupIds: IntArray,
    crcs: IntArray
): ByteBuffer {
    val buffer = ByteBuffer.allocate(count * Int.SIZE_BYTES)
    (0 until count).forEach {
        buffer.putInt(crcs[groupIds[it]])
    }
    return buffer
}

fun encodeGroupNameHashes(
    count: Int,
    isNamed: Boolean,
    groupIds: IntArray,
    nameHashes: IntArray
): ByteBuffer {
    if (isNamed.not()) return ByteBuffer.allocate(0)

    val buffer = ByteBuffer.allocate(count * Int.SIZE_BYTES)
    (0 until count).forEach {
        buffer.putInt(nameHashes[groupIds[it]])
    }
    return buffer
}

private fun encodeFileNameHashes(
    count: Int,
    groupIds: IntArray,
    validFileIds: IntArray,
    isNamed: Boolean,
    fileNameHashes: Array<IntArray>
): ByteBuffer {
    if (isNamed.not()) return ByteBuffer.allocate(0)
    //TODO Figure out a way to calc the number of bytes without doing it like this.
    var bytes = 0
    (0 until count).forEach {
        (0 until validFileIds[groupIds[it]]).forEach { _ ->
            bytes += Int.SIZE_BYTES
        }
    }
    val buffer = ByteBuffer.allocate(bytes)
    (0 until count).forEach {
        val groupId = groupIds[it]
        (0 until validFileIds[groupId]).forEach { fileId ->
            buffer.putInt(fileNameHashes[groupId][fileId])
        }
    }
    return buffer
}