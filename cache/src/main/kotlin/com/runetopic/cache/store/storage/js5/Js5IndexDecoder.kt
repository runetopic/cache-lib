package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.codec.DecompressedArchive
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedIntShortSmart
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.hierarchy.index.group.Group
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal fun DecompressedArchive.decodeJs5Index(
    datFile: IDatFile,
    idxFile: IIdxFile,
    whirlpool: ByteArray,
): Index {
    val buffer = data.toByteBuffer()
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

    val groupIds = buffer.decodeGroupIds(count, protocol)
    val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
    val groupNameHashes = buffer.decodeGroupNameHashes(maxGroupId, count, isNamed, groupIds)
    val groupCrcs = buffer.decodeGroupCrcs(maxGroupId, count, groupIds)
    val groupWhirlpools = buffer.decodeGroupWhirlpools(maxGroupId, isUsingWhirlpool, count, groupIds)
    val groupRevisions = buffer.decodeGroupRevisions(maxGroupId, count, groupIds)
    val groupFileCounts = buffer.decodeGroupFileCounts(maxGroupId, count, groupIds, protocol)
    val fileIds = buffer.decodeFileIds(maxGroupId, groupFileCounts, count, groupIds, protocol)
    val fileNameHashes = buffer.decodeFileNameHashes(maxGroupId, groupFileCounts, count, groupIds, isNamed)

    return Index(
        id = idxFile.id(),
        crc = crc,
        whirlpool = whirlpool,
        compression = compression,
        protocol = protocol,
        revision = revision,
        isNamed = isNamed,
        groups = Array(count) {
            val groupId = groupIds[it]
            Group(
                id = groupId,
                nameHash = groupNameHashes[groupId],
                crc = groupCrcs[groupId],
                whirlpool = groupWhirlpools[groupId],
                revision = groupRevisions[groupId],
                fileCount = groupFileCounts[groupId],
                fileIds = fileIds[groupId],
                fileNameHashes = fileNameHashes[groupId],
                data = datFile.readReferenceTable(idxFile.id(), idxFile.loadReferenceTable(groupId))
            )
        }
    )
}

private tailrec fun ByteBuffer.decodeGroupIds(
    count: Int,
    protocol: Int,
    offset: Int = 0,
    curr: Int = 0,
    groupIds: IntArray = IntArray(count)
): IntArray {
    if (curr == count) return groupIds
    groupIds[curr] = offset + if (protocol >= 7) readUnsignedIntShortSmart() else readUnsignedShort()
    return decodeGroupIds(count, protocol, groupIds[curr], curr + 1, groupIds)
}

private tailrec fun ByteBuffer.decodeGroupFileCounts(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    protocol: Int,
    curr: Int = 0,
    groupFileCounts: IntArray = IntArray(maxGroupId)
): IntArray {
    if (curr == count) return groupFileCounts
    groupFileCounts[groupIds[curr]] = if (protocol >= 7) readUnsignedIntShortSmart() else readUnsignedShort()
    return decodeGroupFileCounts(maxGroupId, count, groupIds, protocol, curr + 1, groupFileCounts)
}

private tailrec fun ByteBuffer.decodeGroupRevisions(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    curr: Int = 0,
    revisions: IntArray = IntArray(maxGroupId)
): IntArray {
    if (curr == count) return revisions
    revisions[groupIds[curr]] = int
    return decodeGroupRevisions(maxGroupId, count, groupIds, curr + 1, revisions)
}

private tailrec fun ByteBuffer.decodeGroupWhirlpools(
    maxGroupId: Int,
    usesWhirlpool: Boolean,
    count: Int,
    groupIds: IntArray,
    curr: Int = 0,
    whirlpools: Array<ByteArray> = Array(maxGroupId) { ByteArray(64) }
): Array<ByteArray> {
    if (!usesWhirlpool) return whirlpools
    if (curr == count) return whirlpools
    whirlpools[groupIds[curr]] = ByteArray(64).apply { get(this) }
    return decodeGroupWhirlpools(maxGroupId, true, count, groupIds, curr + 1, whirlpools)
}

private tailrec fun ByteBuffer.decodeGroupCrcs(
    maxGroupId: Int,
    count: Int,
    groupIds: IntArray,
    curr: Int = 0,
    crcs: IntArray = IntArray(maxGroupId)
): IntArray {
    if (curr == count) return crcs
    crcs[groupIds[curr]] = int
    return decodeGroupCrcs(maxGroupId, count, groupIds, curr + 1, crcs)
}

private tailrec fun ByteBuffer.decodeGroupNameHashes(
    maxGroupId: Int,
    count: Int,
    isNamed: Boolean,
    groupIds: IntArray,
    curr: Int = 0,
    nameHashes: IntArray = IntArray(maxGroupId) { -1 }
): IntArray {
    if (!isNamed) return nameHashes
    if (curr == count) return nameHashes
    nameHashes[groupIds[curr]] = int
    return decodeGroupNameHashes(maxGroupId, count, true, groupIds, curr + 1, nameHashes)
}

private tailrec fun ByteBuffer.decodeFileIds(
    maxGroupId: Int,
    validFileIds: IntArray,
    count: Int,
    groupIds: IntArray,
    protocol: Int,
    curr: Int = 0,
    fileIds: Array<IntArray> = Array(maxGroupId) { IntArray(validFileIds[it]) }
): Array<IntArray> {
    if (curr == count) return fileIds
    val groupId = groupIds[curr]
    decodeValidFileIds(groupId, validFileIds[groupId], protocol, fileIds)
    return decodeFileIds(maxGroupId, validFileIds, count, groupIds, protocol, curr + 1, fileIds)
}

private tailrec fun ByteBuffer.decodeValidFileIds(
    groupId: Int,
    count: Int,
    protocol: Int,
    fileIds: Array<IntArray>,
    offset: Int = 0,
    curr: Int = 0,
) {
    if (curr == count) return
    fileIds[groupId][curr] = offset + if (protocol >= 7) readUnsignedIntShortSmart() else readUnsignedShort()
    return decodeValidFileIds(groupId, count, protocol, fileIds, fileIds[groupId][curr], curr + 1)
}

private tailrec fun ByteBuffer.decodeFileNameHashes(
    maxGroupId: Int,
    validFileIds: IntArray,
    count: Int,
    groupIds: IntArray,
    isNamed: Boolean,
    curr: Int = 0,
    fileNameHashes: Array<IntArray> = Array(maxGroupId) { IntArray(validFileIds[it]) }
): Array<IntArray> {
    if (!isNamed) return fileNameHashes
    if (curr == count) return fileNameHashes
    val groupId = groupIds[curr]
    decodeValidFileNameHashes(groupId, validFileIds[groupId], fileNameHashes)
    return decodeFileNameHashes(maxGroupId, validFileIds, count, groupIds, true, curr + 1, fileNameHashes)
}

private tailrec fun ByteBuffer.decodeValidFileNameHashes(
    groupId: Int,
    count: Int,
    fileNameHashes: Array<IntArray>,
    curr: Int = 0
) {
    if (curr == count) return
    fileNameHashes[groupId][curr] = int
    return decodeValidFileNameHashes(groupId, count, fileNameHashes, curr + 1)
}