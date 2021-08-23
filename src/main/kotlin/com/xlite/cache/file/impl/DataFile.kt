package com.xlite.cache.file.impl

import com.xlite.cache.file.IDataFile
import java.io.File
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class DataFile(private val file: File): IDataFile {
    private val datFile: RandomAccessFile = RandomAccessFile(file, "rw")

    override fun read(id: Int, containerId: Int, sector: Int, length: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun close() = datFile.close()
}