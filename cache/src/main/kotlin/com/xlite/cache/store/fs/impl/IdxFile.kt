package com.xlite.cache.store.fs.impl

import com.xlite.cache.ReferenceTable
import com.xlite.cache.exception.IdxFileException
import com.xlite.cache.store.fs.IIdxFile
import java.io.File
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class IdxFile(
    private val id: Int,
    private val file: File
) : IIdxFile {
    private val idxFile: RandomAccessFile = RandomAccessFile(file, "rw")
    private val readBuffer = ByteArray(ENTRY_LIMIT)

    override fun loadReferenceTable(fileId: Int): ReferenceTable {
        idxFile.seek((fileId * ENTRY_LIMIT).toLong())

        validateHeader()

        val length = (readBuffer[0].toInt() and 0xFF shl 16
                or (readBuffer[1].toInt() and 0xFF shl 8)
                or (readBuffer[2].toInt() and 0xFF))

        val sector = (readBuffer[3].toInt() and 0xFF shl 16
                or (readBuffer[4].toInt() and 0xFF shl 8)
                or (readBuffer[5].toInt() and 0xFF))

        validateSectorLength(length, sector)

        return ReferenceTable(this, fileId, sector, length)
    }

    private fun validateSectorLength(length: Int, sector: Int) {
        if (length <= 0 || sector <= 0) {
            throw IdxFileException("Invalid length or sector Length=$length Sector=$sector")
        }
    }

    private fun validateHeader() {
        if (idxFile.read(readBuffer) != ENTRY_LIMIT) {
            throw IdxFileException("Header does not match Entry limit = $ENTRY_LIMIT")
        }
    }

    override fun validIndexCount(): Int = file.length().toInt() / ENTRY_LIMIT
    override fun id(): Int = id
    override fun close() = idxFile.close()

    private companion object {
        const val ENTRY_LIMIT = 6
    }
}