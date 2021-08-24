package com.xlite.cache.fs.compression.impl

import com.xlite.cache.fs.compression.IFileCodec
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class GZipCodec: IFileCodec {
    override fun compress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        val inputStream = ByteArrayInputStream(data)
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { os -> IOUtils.copy(inputStream, os) }
        return outputStream.toByteArray()
    }

    override fun decompress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPInputStream(ByteArrayInputStream(data, 0, length)).use {
            IOUtils.copy(it, outputStream)
        }
        return outputStream.toByteArray()
    }
}