package com.runetopic.cache.codec.impl

import com.runetopic.cache.codec.FileCodec
import com.runetopic.cache.store.Constants.BZIP_HEADER
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
internal class BZip2Codec : FileCodec {
    override fun compress(data: ByteArray, keys: IntArray): ByteArray {
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

    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray {
        val buffer = ByteArray(length + BZIP_HEADER.size)

        System.arraycopy(BZIP_HEADER, 0, buffer, 0, BZIP_HEADER.size)
        System.arraycopy(data, 0, buffer, BZIP_HEADER.size, length)

        val stream = ByteArrayOutputStream()
        BZip2CompressorInputStream(ByteArrayInputStream(buffer)).use { IOUtils.copy(it, stream) }
        return stream.toByteArray()
    }
}
