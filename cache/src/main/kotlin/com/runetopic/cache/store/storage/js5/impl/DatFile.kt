package com.runetopic.cache.store.storage.js5.impl

import com.runetopic.cache.exception.DatFileException
import com.runetopic.cache.exception.EndOfDatFileException
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedMedium
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.Constants.DAT_SIZE
import com.runetopic.cache.store.storage.js5.IDatFile
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Path

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal class DatFile(
    path: Path
) : IDatFile {
    private val datFile: RandomAccessFile = RandomAccessFile(path.toFile(), "rw")
    private val datBuffer = ByteArray(datFile.length().toInt())

    init {
        datFile.readFully(datBuffer)
    }

    override fun readReferenceTable(
        id: Int,
        referenceTable: ReferenceTable
    ): ByteArray {
        var sector = referenceTable.sector
        val length = referenceTable.length
        val referenceTableId = referenceTable.id

        if (validateSector(sector)) return byteArrayOf()

        val buffer = ByteBuffer.allocate(length)

        var part = 0
        var bytes = 0
        while (length > bytes) {
            if (sector == 0) {
                throw EndOfDatFileException("Unexpected end of file. Id=[$id} Length=[$length]")
            }

            val offset = DAT_SIZE * sector
            val large = referenceTableId > 0xFFFF
            val headerSize = if (large) 10 else 8
            val blockSize = adjustBlockLength(length - bytes, headerSize)
            val header = datBuffer.copyOfRange(offset, offset + headerSize + blockSize).toByteBuffer()

            val currentReferenceTableId = if (large) header.int else header.readUnsignedShort()
            val currentPart = header.readUnsignedShort()
            val nextSector = header.readUnsignedMedium()
            val currentIndex = header.readUnsignedByte()

            if (referenceTableId != currentReferenceTableId || currentPart != part || id != currentIndex) {
                throw DatFileException("DatFile mismatch Id={$currentIndex} != {$id}, ReferenceTableId={$currentReferenceTableId} != {$referenceTableId}, CurrentPart={$currentPart} != {$part}")
            }
            if (nextSector < 0 || datFile.length() / DAT_SIZE < nextSector) {
                throw DatFileException("Invalid next sector $nextSector")
            }

            buffer.put(header.array(), headerSize, blockSize)
            bytes += blockSize
            sector = nextSector
            ++part
        }

        buffer.flip()
        return buffer.array()
    }

    private fun adjustBlockLength(
        blockLength: Int,
        headerLength: Int
    ): Int {
        return if (blockLength <= DAT_SIZE - headerLength) blockLength else DAT_SIZE - headerLength
    }

    private fun validateSector(sector: Int): Boolean = (sector <= 0L || datFile.length() / DAT_SIZE < sector)

    override fun close() = datFile.close()
}
