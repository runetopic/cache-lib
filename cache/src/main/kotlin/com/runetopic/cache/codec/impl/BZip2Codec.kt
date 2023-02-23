package com.runetopic.cache.codec.impl

import com.runetopic.cache.codec.ArchiveCodec
import com.runetopic.cache.store.Constants.BZIP_HEADER
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class BZip2Codec : ArchiveCodec {
    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray = with(ByteArray(length + BZIP_HEADER.size)) {
        System.arraycopy(BZIP_HEADER, 0, this, 0, BZIP_HEADER.size)
        System.arraycopy(data, 0, this, BZIP_HEADER.size, length)
        ByteArrayOutputStream()
            .apply { BZip2CompressorInputStream(ByteArrayInputStream(this@with)).use { IOUtils.copy(it, this) } }
            .toByteArray()
    }
}
