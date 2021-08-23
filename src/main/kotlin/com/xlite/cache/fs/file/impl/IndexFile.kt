package com.xlite.cache.fs.file.impl

import com.xlite.cache.fs.file.IIndexFile
import java.io.File
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
open class IndexFile(open val file: File): IIndexFile {
    private val indexFile: RandomAccessFile = RandomAccessFile(file, "rw")

    override fun length(): Int = file.length().toInt() / ENTRY_LIMIT
    override fun close() = indexFile.close()

    private companion object {
        private const val ENTRY_LIMIT = 6
    }
}