package com.runetopic.cache.store.storage.js5.io.idx

import com.runetopic.cache.exception.IdxFileException
import com.runetopic.cache.extension.readUnsignedMedium
import com.runetopic.cache.extension.toByteBuffer
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.store.Constants.IDX_SIZE
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
    private val path: Path
) : IIdxFile {
    private val idxFile: RandomAccessFile = RandomAccessFile(path.toFile(), "rw")
    private val idxBuffer = ByteArray(idxFile.length().toInt()).also { idxFile.readFully(it) }

    override fun decode(id: Int): ReferenceTable {
        if (idxBuffer.size < id * IDX_SIZE + 6) {
            return ReferenceTable(id, 0, 0)
        }
        val position = id * IDX_SIZE
        val buffer = idxBuffer.copyOfRange(position, position + IDX_SIZE).toByteBuffer()
        val length = buffer.readUnsignedMedium()
        val sector = buffer.readUnsignedMedium()
        if (length < 0) {
            throw IdxFileException("Invalid length for sector Length=$length Sector=$sector")
        }
        return ReferenceTable(id, sector, length)
    }

    override fun encode(data: ByteArray) {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalPathApi::class)
    override fun validIndexCount(): Int = path.fileSize().toInt() / IDX_SIZE
    override fun id(): Int = id
    override fun close() = idxFile.close()
}