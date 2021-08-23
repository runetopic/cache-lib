package com.xlite.cache.fs.file.impl

import com.xlite.cache.exception.IndexFileException
import com.xlite.cache.fs.file.IIndexFile
import com.xlite.cache.fs.file.ReferenceTable
import java.io.File
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class IndexFile(private val id: Int, open val file: File): IIndexFile {
    private val indexFile: RandomAccessFile = RandomAccessFile(file, "rw")
    private val buffer: ByteArray = ByteArray(ENTRY_LIMIT)

    override fun read(id: Int): ReferenceTable {
        indexFile.seek((id * ENTRY_LIMIT).toLong())

        validateHeader()

        val length = (buffer[0].toInt() and 0xFF) shl 16 xor
                (buffer[1].toInt() and 0xFF shl 8) xor
                (buffer[2].toInt() and 0xFF)
        val sector = (buffer[3].toInt() and 0xFF) shl 16 xor
                (buffer[4].toInt() and 0xFF shl 8) xor
                (buffer[5].toInt() and 0xFF)

        validateSectorLength(length, sector)

        return ReferenceTable(this, id, sector, length)
    }

    private fun validateSectorLength(length: Int, sector: Int) {
        if (length <= 0 || sector <= 0) {
            throw IndexFileException("Invalid length or sector Length=$length Sector=$sector")
        }
    }

    private fun validateHeader() {
        if (indexFile.read(buffer) != ENTRY_LIMIT) {
            throw IndexFileException("Header does not match Entry limit = $ENTRY_LIMIT")
        }
    }

    override fun length(): Int = file.length().toInt() / ENTRY_LIMIT
    override fun indexId(): Int = id
    override fun close() = indexFile.close()

    private companion object {
        private const val ENTRY_LIMIT = 6
    }
}