package com.runetopic.cache.store.js5.impl

import com.runetopic.cache.exception.IdxFileException
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.js5.IIdxFile
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

    @Synchronized
    override fun loadReferenceTable(id: Int): ReferenceTable {
        idxFile.seek((id * ENTRY_LIMIT).toLong())
        validateHeader()
        val length = (readBuffer[0].toInt() and 0xFF shl 16
                or (readBuffer[1].toInt() and 0xFF shl 8)
                or (readBuffer[2].toInt() and 0xFF))

        val sector = (readBuffer[3].toInt() and 0xFF shl 16
                or (readBuffer[4].toInt() and 0xFF shl 8)
                or (readBuffer[5].toInt() and 0xFF))
        validateLengthAtSector(length, sector)
        return ReferenceTable(this, id, sector, length)
    }

    private fun validateLengthAtSector(length: Int, sector: Int) {
        if (length < 0) {
            throw IdxFileException("Invalid length for sector Length=$length Sector=$sector")
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