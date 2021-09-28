package com.runetopic.cache.store.storage.js5.impl

import com.runetopic.cache.exception.DatFileException
import com.runetopic.cache.exception.EndOfDatFileException
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.Constants.SECTOR_SIZE
import com.runetopic.cache.store.storage.js5.IDatFile
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Path

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class DatFile(
    path: Path
): IDatFile {
    private val datFile: RandomAccessFile = RandomAccessFile(path.toFile(), "rw")

    @Synchronized
    override fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray {
        var sector = referenceTable.sector
        val length = referenceTable.length
        val referenceTableId = referenceTable.id

        if (validateSector(sector).not()) return byteArrayOf()

        val buffer = ByteBuffer.allocate(length)
        val readBuffer = ByteBuffer.wrap(ByteArray(SECTOR_SIZE))

        var part = 0
        var readBytes = 0
        var nextSector: Int

        while (length > readBytes) {
            if (sector == 0) {
                throw EndOfDatFileException("Unexpected end of file. Id=[$id} Length=[$length]")
            }

            datFile.seek((SECTOR_SIZE * sector).toLong())

            var blockLength = length - readBytes
            var headerLength: Byte
            var currentIndex: Int
            var currentPart: Int
            var currentReferenceTableId: Int

            if (referenceTableId > 0xFFFF) {
                headerLength = 10
                blockLength = adjustBlockLength(blockLength, headerLength)

                validateHeader(readBuffer.array(), headerLength, blockLength, id, referenceTableId)

                currentReferenceTableId = (readBuffer[0].toInt() and 0xFF shl 24
                        or (readBuffer[1].toInt() and 0xFF shl 16)
                        or (readBuffer[2].toInt() and 0xFF shl 8)
                        or (readBuffer[3].toInt() and 0xFF))
                currentPart = ((readBuffer[4].toInt() and 0xFF) shl 8) + (readBuffer[5].toInt() and 0xFF)
                nextSector = (readBuffer[6].toInt() and 0xFF shl 16
                        or (readBuffer[7].toInt() and 0xFF shl 8)
                        or (readBuffer[8].toInt() and 0xFF))
                currentIndex = (readBuffer[9].toInt() and 0xFF)

            } else {
                headerLength = 8
                blockLength = adjustBlockLength(blockLength, headerLength)

                validateHeader(readBuffer.array(), headerLength, blockLength, id, referenceTableId)

                currentReferenceTableId = (readBuffer[0].toInt() and 0xFF shl 8
                        or (readBuffer[1].toInt() and 0xFF))
                currentPart = ((readBuffer[2].toInt() and 0xFF) shl 8) + (readBuffer[3].toInt() and 0xFF)
                nextSector = (readBuffer[4].toInt() and 0xFF shl 16
                        or (readBuffer[5].toInt() and 0xFF shl 8)
                        or (readBuffer[6].toInt() and 0xFF))
                currentIndex = (readBuffer[7].toInt() and 0xFF)
            }

            validateData(referenceTableId, currentReferenceTableId, currentPart, part, id, currentIndex)
            validateNextSector(nextSector)

            buffer.put(readBuffer.array(), headerLength.toInt(), blockLength)
            readBytes += blockLength
            sector = nextSector
            ++part
        }

        buffer.flip()
        return buffer.array()
    }

    private fun adjustBlockLength(blockLength: Int, headerLength: Byte): Int {
        return if (blockLength <= SECTOR_SIZE - headerLength) {
            blockLength
        } else {
            SECTOR_SIZE - headerLength
        }
    }

    private fun validateData(
        referenceTableId: Int,
        currentReferenceTableId: Int,
        currentPart: Int,
        part: Int,
        id: Int,
        currentId: Int,
    ) {
        if (referenceTableId != currentReferenceTableId || currentPart != part || id != currentId) {
            throw DatFileException("DataFile mismatch Id={${currentId}} != {${id}}, ReferenceTableId={${currentReferenceTableId}} != {${referenceTableId}}, CurrentPart={${currentPart}} != {${part}}")
        }
    }

    private fun validateNextSector(nextSector: Int) {
        if (nextSector < 0 || datFile.length() / SECTOR_SIZE < nextSector) {
            throw DatFileException("Invalid next sector $nextSector")
        }
    }

    private fun validateHeader(
        buffer: ByteArray,
        headerLength: Byte,
        blockLength: Int,
        id: Int,
        referenceTableId: Int,
    ) {
        if (datFile.read(buffer, 0, headerLength + blockLength) != headerLength + blockLength) {
            throw DatFileException("Header length mismatch for Id=[$id] ReferenceTableId=[$referenceTableId]")
        }
    }

    private fun validateSector(sector: Int): Boolean {
        return !(sector <= 0L || datFile.length() / SECTOR_SIZE < sector)
    }

    override fun close() = datFile.close()
}