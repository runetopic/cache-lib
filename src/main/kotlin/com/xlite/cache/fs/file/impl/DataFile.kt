package com.xlite.cache.fs.file.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.constant.FileConstants.SECTOR_SIZE
import com.xlite.cache.exception.DataFileException
import com.xlite.cache.exception.EndOfFileException
import com.xlite.cache.fs.file.IDataFile
import com.xlite.cache.fs.file.ReferenceTable
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class DataFile(file: File): IDataFile  {
    private val datFile: RandomAccessFile = RandomAccessFile(file, "rw")

    override fun read(id: Int, referenceTable: ReferenceTable): ByteArray {
        var sector = referenceTable.sector
        val length = referenceTable.length
        val archiveId = referenceTable.archiveId

        validateSector(sector, length)

        val readBuffer = ByteArray(SECTOR_SIZE)
        val buffer = ByteBuffer.allocate(length)

        var part = 0
        var readBytes = 0
        var nextSector: Int

        while (length > readBytes) {
            if (sector == 0) {
                throw EndOfFileException("Unexpected end of file. Id=[$id} ArchiveId=[$archiveId] Length=[$length]")
            }

            datFile.seek((SECTOR_SIZE * sector).toLong())

            var blockLength = length - readBytes
            var headerSize: Byte
            var currentIndex: Int
            var currentPart: Int
            var currentContainerId: Int

            if (archiveId > 0xFFFF) {
                headerSize = 10

                if (blockLength > SECTOR_SIZE - headerSize) {
                    blockLength = SECTOR_SIZE - headerSize
                }

                validateHeader(readBuffer, headerSize, blockLength, id, archiveId)

                currentContainerId = (readBuffer[0].toInt() and 0xFF shl 24
                        or (readBuffer[1].toInt() and 0xFF shl 16)
                        or (readBuffer[2].toInt() and 0xFF shl 8)
                        or (readBuffer[3].toInt() and 0xFF))
                currentPart = ((readBuffer[4].toInt() and 0xFF) shl 8) + (readBuffer[5].toInt() and 0xFF)
                nextSector = (readBuffer[6].toInt() and 0xFF shl 16
                        or (readBuffer[7].toInt() and 0xFF shl 8)
                        or (readBuffer[8].toInt() and 0xFF))
                currentIndex = readBuffer[9].toInt() and 0xFF
            } else {
                headerSize = 8

                if (blockLength > SECTOR_SIZE - headerSize) {
                    blockLength = SECTOR_SIZE - headerSize
                }

                validateHeader(readBuffer, headerSize, blockLength, id, archiveId)

                currentContainerId = ((readBuffer[0].toInt() and 0xFF shl 8) or (readBuffer[1].toInt() and 0xFF))
                currentPart = ((readBuffer[2].toInt() and 0xFF shl 8) or (readBuffer[3].toInt() and 0xFF))
                nextSector = ((readBuffer[4].toInt() and 0xFF) shl 16
                        or (readBuffer[5].toInt() and 0xFF shl 8)
                        or (readBuffer[6].toInt() and 0xFF))
                currentIndex = readBuffer[7].toInt() and 0xFF
            }

            validateData(archiveId, currentContainerId, currentPart, part, id, currentIndex)
            validateNextSector(nextSector)

            buffer.put(readBuffer, headerSize.toInt(), blockLength)
            readBytes += blockLength
            sector = nextSector
            ++part
        }

        buffer.flip()
        return buffer.array()
    }

    private fun validateData(
        archiveId: Int,
        currentArchiveId: Int,
        currentPart: Int,
        part: Int,
        index: Int,
        currentIndex: Int,
    ) {
        if (archiveId != currentArchiveId || currentPart != part || index != currentIndex) {
           throw DataFileException("DataFile miss-match Id={${currentIndex}} != {${index}}, ArchiveId={${currentArchiveId}} != {${archiveId}}, CurrentPart={${currentPart}} != {${part}}")
        }
    }

    private fun validateNextSector(nextSector: Int) {
        if (nextSector < 0 || datFile.length() / SECTOR_SIZE < nextSector) {
            throw DataFileException("Invalid next sector $nextSector")
        }
    }

    private fun validateHeader(
        buffer: ByteArray,
        headerLength: Byte,
        blockLength: Int,
        id: Int,
        containerId: Int,
    ) {
        if (datFile.read(buffer, 0, headerLength + blockLength) != headerLength + blockLength) {
            throw DataFileException("Header length miss-match for Id=[$id] Container=[$containerId]")
        }
    }

    private fun validateSector(sector: Int, length: Int) {
        if (sector <= 0L || datFile.length() / SECTOR_SIZE < sector) {
            throw DataFileException("Could not read $length for sector $sector")
        }
    }

    override fun close() = datFile.close()

    private companion object {
        private val logger = InlineLogger()
    }
}