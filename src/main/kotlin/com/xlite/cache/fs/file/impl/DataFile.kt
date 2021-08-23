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
        val sector = referenceTable.sector
        val length = referenceTable.length
        val containerId = referenceTable.id

        validateSector(sector, length)

        val buffer = ByteArray(SECTOR_SIZE)
        val byteBuffer = ByteBuffer.allocate(length)

        var part = 0
        var readBytes = 0
        var nextSector: Int

        while (length > readBytes) {
            if (sector == 0) {
                throw EndOfFileException("Unexpected end of file. Id=[$id} Container=[$containerId] Length=[$length]")
            }

            datFile.seek((SECTOR_SIZE * sector).toLong())

            var blockLength = length - readBytes
            val headerLength: Byte
            val index: Int
            val currentPart: Int
            val currentContainerId: Int

            if (containerId > 0xFFFF) {
                headerLength = 10

                if (blockLength > SECTOR_SIZE - length) {
                    blockLength = SECTOR_SIZE - length
                }

                validateHeader(buffer, headerLength, blockLength, id, containerId)

                currentContainerId = (buffer[0].toInt() and 0xFF shl 24
                        or (buffer[1].toInt() and 0xFF shl 16)
                        or (buffer[2].toInt() and 0xFF shl 8)
                        or (buffer[3].toInt() and 0xFF))
                currentPart = ((buffer[4].toInt() and 0xFF) shl 8) + (buffer[5].toInt() and 0xFF)
                nextSector = ((buffer[6].toInt() and 0xFF) shl 16)
                index = buffer[9].toInt() and 0xFF
            } else {
                headerLength = 8

                if (blockLength > SECTOR_SIZE - headerLength) {
                    blockLength = SECTOR_SIZE - headerLength
                }

                validateHeader(buffer, headerLength, blockLength, id, containerId)

                currentContainerId = (buffer[0].toInt() and 0xFF shl 8 or (buffer[1].toInt() and 0xFF))
                currentPart = (buffer[2].toInt() and 0xFF shl 8 or (buffer[3].toInt() and 0xFF))
                nextSector = (buffer[4].toInt() and 0xFF shl 16
                        or (buffer[5].toInt() and 0xFF shl 8)
                        or (buffer[6].toInt() and 0xFF))
                index = buffer[7].toInt() and 0xFF
            }

            validateData(containerId, currentContainerId, currentPart, part, id, index)
            validateNextSector(nextSector)

            byteBuffer.put(buffer, headerLength.toInt(), blockLength)
            readBytes += blockLength

            ++part
        }

        byteBuffer.flip()
        return byteBuffer.array()
    }

    private fun validateData(
        containerId: Int,
        currentContainerId: Int,
        currentPart: Int,
        part: Int,
        id: Int,
        index: Int,
    ) {
        if (containerId != currentContainerId || currentPart != part || id != index) {
            throw DataFileException("DataFile miss-match Container={${containerId}} != {${currentContainerId}}, CurrentPart={${currentPart}} != {${part}}, Id={${id}} != {${index}}")
        }
    }

    private fun validateNextSector(nextSector: Int) {
        if (datFile.length() / SECTOR_SIZE < nextSector) {
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