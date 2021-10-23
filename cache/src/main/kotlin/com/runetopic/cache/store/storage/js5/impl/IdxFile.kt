package com.runetopic.cache.store.storage.js5.impl

import com.runetopic.cache.exception.IdxFileException
import com.runetopic.cache.extension.readUnsignedMedium
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.storage.js5.IIdxFile
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.fileSize

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal class IdxFile(
    private val id: Int,
    private val path: Path
) : IIdxFile {
    private val idxFile: RandomAccessFile = RandomAccessFile(path.toFile(), "rw")
    private val idxBuffer = ByteArray(idxFile.length().toInt())

    init {
        idxFile.readFully(idxBuffer)
    }

    override fun loadReferenceTable(id: Int): ReferenceTable {
        val offset = id * ENTRY_LIMIT
        val buffer = ByteBuffer.wrap(idxBuffer.copyOfRange(offset, offset + 6))
        val length = buffer.readUnsignedMedium()
        val sector = buffer.readUnsignedMedium()
        if (length < 0) {
            throw IdxFileException("Invalid length for sector Length=$length Sector=$sector")
        }
        return ReferenceTable(this, id, sector, length)
    }

    @OptIn(ExperimentalPathApi::class)
    override fun validIndexCount(): Int = path.fileSize().toInt() / ENTRY_LIMIT
    override fun id(): Int = id
    override fun close() = idxFile.close()

    private companion object {
        const val ENTRY_LIMIT = 6
    }
}