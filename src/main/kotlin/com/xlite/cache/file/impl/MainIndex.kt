package com.xlite.cache.file.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.extension.inject
import com.xlite.cache.file.IFileIndex
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class MainIndex(private val file: RandomAccessFile): IFileIndex {
    private val logger by inject<InlineLogger>()

    override fun decode(): ByteArray {
        logger.debug { "Decoding main index" }
        return byteArrayOf(1)
    }

    override fun length(): Int = file.length().toInt()  / 6
}