package com.runetopic.cache.store.storage.js5.impl

import com.runetopic.cache.exception.IdxFileException
import com.runetopic.cache.extension.readUnsignedMedium
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.Constants.IDX_SIZE
import com.runetopic.cache.store.storage.js5.IIdxFile
import java.io.RandomAccessFile
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
    private val path: Path,
    private val idxBuffer: ByteArray = path.toFile().readBytes()
) : IIdxFile {
    override fun loadReferenceTable(id: Int): ReferenceTable {
        val offset = id * IDX_SIZE
        val buffer = idxBuffer.copyOfRange(offset, offset + IDX_SIZE).toByteBuffer()
        val length = buffer.readUnsignedMedium()
        val sector = buffer.readUnsignedMedium()
        if (length < 0) {
            throw IdxFileException("Invalid length for sector Length=$length Sector=$sector")
        }
        return ReferenceTable(id, sector, length)
    }

    override fun validIndexCount(): Int = path.fileSize().toInt() / IDX_SIZE
    override fun id(): Int = id
}
