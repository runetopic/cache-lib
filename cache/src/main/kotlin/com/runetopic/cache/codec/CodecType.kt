package com.runetopic.cache.codec

import com.runetopic.cache.codec.impl.BZip2Codec
import com.runetopic.cache.codec.impl.GZipCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal class CodecType {
    companion object {
        val bzip = BZip2Codec()
        val gzip = GZipCodec()
    }
}
