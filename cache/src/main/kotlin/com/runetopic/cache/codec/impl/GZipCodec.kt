package com.runetopic.cache.codec.impl

import com.runetopic.cache.codec.IFileCodec
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class GZipCodec : IFileCodec {
    override fun compress(data: ByteArray, keys: IntArray): ByteArray {
        val inputStream = ByteArrayInputStream(data)
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { os -> IOUtils.copy(inputStream, os) }
        return outputStream.toByteArray()
    }

    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPInputStream(ByteArrayInputStream(data, 0, length)).use {
            IOUtils.copy(it, outputStream)
        }
        return outputStream.toByteArray()
    }
}
