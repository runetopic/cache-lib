package com.runetopic.cache.codec.impl

import com.runetopic.cache.codec.ArchiveCodec
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class GZipCodec : ArchiveCodec {
    override fun decompress(data: ByteArray, length: Int, keys: IntArray): ByteArray = ByteArrayOutputStream()
        .apply { GZIPInputStream(ByteArrayInputStream(data, 0, length)).use { IOUtils.copy(it, this) } }
        .toByteArray()
}
