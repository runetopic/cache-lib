package com.runetopic.cache

import com.runetopic.cache.compression.Compression
import com.runetopic.cache.exception.ProtocolException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.store.fs.impl.IdxFile
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal data class ReferenceTable(
    val idxFile: IdxFile,
    val fileId: Int,
    val sector: Int,
    val length: Int,
) {
    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.idxFile)
        hash = 19 * hash + this.fileId
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> {
                return false
            }
            else -> return when {
                javaClass != other.javaClass -> false
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    when {
                        idxFile != referenceTable.idxFile -> false
                        fileId != referenceTable.fileId -> false
                        sector != referenceTable.sector -> false
                        this.length != referenceTable.length -> false
                        else -> true
                    }
                }
            }
        }
    }

    fun loadGroup(groupId: Int, whirlpool: ByteArray, data: ByteArray): Js5Group {
        val container = Compression.decompress(data, emptyArray())
        val buffer = ByteBuffer.wrap(container.data)
        val protocol = buffer.readUnsignedByte()
        var revision = 0

        if (protocol < 5 || protocol > 6) {
            throw ProtocolException("Unhandled protocol $protocol when reading index $this")
        }

        if (protocol >= 6) {
            revision = buffer.int
        }

        val hash = buffer.readUnsignedByte()
        val isNamed = (0x1 and hash) != 0
        val isUsingWhirlPool = (0x2 and hash) != 0

        val count = buffer.readUnsignedShort()

        var lastFileId = 0
        var biggestFileId = -1

        val validFileIds = IntArray(count)

        (0 until count).forEach {
            validFileIds[it] = buffer.readUnsignedShort().let { id -> lastFileId += id; lastFileId }
            if (validFileIds[it] > biggestFileId) biggestFileId = validFileIds[it]
        }

        val nameHashes = nameHashes(biggestFileId, count, isNamed, validFileIds, buffer)
        val crcs = crcs(biggestFileId, count, validFileIds, buffer)
        val whirlpools = whirlpools(isUsingWhirlPool, count, buffer, validFileIds)
        val revisions = revisions(biggestFileId, count, validFileIds, buffer)
        val validEntryIds = validEntryIds(biggestFileId, count, validFileIds, buffer)
        val entries = entries(biggestFileId, validEntryIds, count, validFileIds, buffer, isNamed)

        val files = mutableListOf<Js5File>()
        (0 until count).forEach {
            files.add(Js5File(
                groupId = groupId,
                fileId = it,
                nameHash = if (isNamed) nameHashes[validFileIds[it]] else -1,
                crc = crcs[validFileIds[it]],
                whirlpool = if (isUsingWhirlPool) whirlpools[validFileIds[it]] else byteArrayOf(),
                revision = revisions[validFileIds[it]],
                keys = intArrayOf(),
                entries = entries[it]
            ))
        }
        return Js5Group(groupId, container.crc, whirlpool, container.compression, protocol, revision, isNamed, files)
    }

    private fun entries(
        biggestFileId: Int,
        validEntryIds: IntArray,
        count: Int,
        validFileIds: IntArray,
        buffer: ByteBuffer,
        isNamed: Boolean,
    ): Array<Array<Js5FileEntry>> {
        val entries = Array(biggestFileId + 1) { row -> Array(validEntryIds[row]) { Js5FileEntry() } }

        (0 until count).forEach {
            val fileId = validFileIds[it]
            var currEntryId = 0
            (0 until validEntryIds[fileId]).forEach { entryId ->
                buffer.readUnsignedShort()
                    .let { id -> currEntryId += id; currEntryId }
                    .also { entries[fileId][entryId] = Js5FileEntry(fileId, currEntryId) }
            }
        }

        if (isNamed) {
            (0 until count).forEach {
                val fileId = validFileIds[it]
                (0 until validEntryIds[fileId]).forEach { entryId ->
                    //TODO Construct the entry with the namehash instead of setting it afterward.
                    entries[fileId][entryId].nameHash = buffer.int
                }
            }
        }
        return entries
    }

    private fun validEntryIds(
        biggestFileId: Int,
        count: Int,
        validFileIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val validEntryIds = IntArray(biggestFileId + 1)
        (0 until count).forEach {
            validEntryIds[validFileIds[it]] = buffer.readUnsignedShort()
        }
        return validEntryIds
    }

    private fun revisions(
        biggestFileId: Int,
        count: Int,
        validFileIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val revisions = IntArray(biggestFileId + 1)
        (0 until count).forEach {
            revisions[validFileIds[it]] = buffer.int
        }
        return revisions
    }

    private fun whirlpools(
        usesWhirlpool: Boolean,
        count: Int,
        buffer: ByteBuffer,
        validFileIds: IntArray,
    ): Array<ByteArray> {
        val whirlpools = Array(64) { byteArrayOf() }
        if (usesWhirlpool) {
            (0 until count).forEach {
                val whirlpool = ByteArray(64)
                buffer.get(whirlpool)
                whirlpools[validFileIds[it]] = whirlpool
            }
        }
        return whirlpools
    }

    private fun crcs(
        biggestFileId: Int,
        count: Int,
        validFileIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val crcs = IntArray(biggestFileId + 1)
        (0 until count).forEach {
            crcs[validFileIds[it]] = buffer.int
        }
        return crcs
    }

    private fun nameHashes(
        biggestFileId: Int,
        count: Int,
        isNamed: Boolean,
        validFileIds: IntArray,
        buffer: ByteBuffer,
    ): IntArray {
        val nameHashes = IntArray(biggestFileId + 1) { -1 }

        if (isNamed.not()) return nameHashes

        (0 until count).forEach {
            nameHashes[validFileIds[it]] = buffer.int
        }
        return nameHashes
    }
}