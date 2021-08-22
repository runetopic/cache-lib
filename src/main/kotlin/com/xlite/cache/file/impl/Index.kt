package com.xlite.cache.file.impl

import com.xlite.cache.file.IFileContainer
import com.xlite.cache.file.IFileIndex
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Index(val file: RandomAccessFile): IFileIndex {

    private val containers = arrayListOf<IFileContainer>()

    override fun decode(): ByteArray {
        return byteArrayOf(1)
    }

    override fun length(): Int {
        TODO("Not yet implemented")
    }
}