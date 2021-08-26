package com.xlite.cache.fs.file.impl

import com.xlite.cache.exception.IndexFileException
import com.xlite.cache.extension.readMedium
import com.xlite.cache.fs.ReferenceTable
import com.xlite.cache.fs.file.IIndexFile
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class IndexFile(private val id: Int, private val file: File): IIndexFile {
    private val indexFile: RandomAccessFile = RandomAccessFile(file, "rw")
    private val buffer = ByteBuffer.wrap(ByteArray(ENTRY_LIMIT))

    override fun loadReferenceTable(id: Int): ReferenceTable {
        indexFile.seek((id * ENTRY_LIMIT).toLong())

        validateHeader()

        val length = buffer.readMedium(0)
        val sector = buffer.readMedium(3)

        validateSectorLength(length, sector)

        return ReferenceTable(this, id, sector, length)
    }

    private fun validateSectorLength(length: Int, sector: Int) {
        if (length <= 0 || sector <= 0) {
            throw IndexFileException("Invalid length or sector Length=$length Sector=$sector")
        }
    }

    private fun validateHeader() {
        if (indexFile.read(buffer.array()) != ENTRY_LIMIT) {
            throw IndexFileException("Header does not match Entry limit = $ENTRY_LIMIT")
        }
    }

    override fun validIndexCount(): Int = file.length().toInt() / ENTRY_LIMIT
    override fun id(): Int = id
    override fun close() = indexFile.close()

    private companion object {
        const val ENTRY_LIMIT = 6
    }
}