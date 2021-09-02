package com.xlite.cache.compression.impl

import com.xlite.cache.constant.FileConstants.BZIP_HEADER
import com.xlite.cache.compression.IFileCodec
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class BZip2Codec: IFileCodec {
    override fun compress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        val stream: InputStream = ByteArrayInputStream(data)
        val bout = ByteArrayOutputStream()
        BZip2CompressorOutputStream(bout, 1).use { os -> IOUtils.copy(stream, os) }

        val buffer = bout.toByteArray()

        assert(BZIP_HEADER[0] == buffer[0])
        assert(BZIP_HEADER[1] == buffer[1])
        assert(BZIP_HEADER[2] == buffer[2])
        assert(BZIP_HEADER[3] == buffer[3])

        return Arrays.copyOfRange(buffer, BZIP_HEADER.size, buffer.size)
    }

    override fun decompress(data: ByteArray, length: Int, keys: Array<Int>): ByteArray {
        val buffer = ByteArray(length + BZIP_HEADER.size)

        System.arraycopy(BZIP_HEADER, 0, buffer,0, BZIP_HEADER.size)
        System.arraycopy(data, 0, buffer, BZIP_HEADER.size, length)

        val stream = ByteArrayOutputStream()

        BZip2CompressorInputStream(ByteArrayInputStream(buffer)).use {
            IOUtils.copy(it, stream)
        }

        return stream.toByteArray()
    }
}