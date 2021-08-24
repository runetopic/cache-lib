package com.xlite.cache.fs.file.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.constant.FileConstants.SECTOR_SIZE
import com.xlite.cache.exception.DataFileException
import com.xlite.cache.exception.EndOfFileException
import com.xlite.cache.extension.readInt
import com.xlite.cache.extension.readMedium
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.fs.file.IDataFile
import com.xlite.cache.fs.ReferenceTable
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class DataFile(file: File): IDataFile  {
    private val datFile: RandomAccessFile = RandomAccessFile(file, "rw")

    override fun readReferenceTable(id: Int, referenceTable: ReferenceTable): ByteArray {
        var sector = referenceTable.sector
        val length = referenceTable.length
        val archiveId = referenceTable.archiveId

        validateSector(sector, length)

        val buffer = ByteBuffer.allocate(length)
        val readBuffer = ByteBuffer.wrap(ByteArray(SECTOR_SIZE))

        var part = 0
        var readBytes = 0
        var nextSector: Int

        while (length > readBytes) {
            if (sector == 0) {
                throw EndOfFileException("Unexpected end of file. Id=[$id} ArchiveId=[$archiveId] Length=[$length]")
            }

            datFile.seek((SECTOR_SIZE * sector).toLong())

            var blockLength = length - readBytes
            var headerLength: Byte
            var currentIndex: Int
            var currentPart: Int
            var currentContainerId: Int

            if (archiveId > 0xFFFF) {
                headerLength = 10
                blockLength = adjustBlockLength(blockLength, headerLength)

                validateHeader(readBuffer.array(), headerLength, blockLength, id, archiveId)

                currentContainerId = readBuffer.readInt(0)
                currentPart = readBuffer.readUnsignedShort(4)
                nextSector = readBuffer.readMedium(6)
                currentIndex = readBuffer.readUnsignedByte(9)
            } else {
                headerLength = 8
                blockLength = adjustBlockLength(blockLength, headerLength)

                validateHeader(readBuffer.array(), headerLength, blockLength, id, archiveId)

                currentContainerId = readBuffer.readUnsignedShort(0)
                currentPart = readBuffer.readUnsignedShort(2)
                nextSector = readBuffer.readMedium(4)
                currentIndex = readBuffer.readUnsignedByte(7)
            }

            validateData(archiveId, currentContainerId, currentPart, part, id, currentIndex)
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
        archiveId: Int,
        currentArchiveId: Int,
        currentPart: Int,
        part: Int,
        index: Int,
        currentIndex: Int,
    ) {
        if (archiveId != currentArchiveId || currentPart != part || index != currentIndex) {
           throw DataFileException("DataFile mismatch Id={${currentIndex}} != {${index}}, ArchiveId={${currentArchiveId}} != {${archiveId}}, CurrentPart={${currentPart}} != {${part}}")
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
            throw DataFileException("Header length mismatch for Id=[$id] Container=[$containerId]")
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