package com.runetopic.cache.codec

import com.runetopic.cache.codec.impl.BZip2Codec
import com.runetopic.cache.codec.impl.GZipCodec
import com.runetopic.cache.codec.impl.NoFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal sealed class CodecType(
    val codec: FileCodec
) {
    object BadCodec : CodecType(NoFileCodec())
    object NoCodec : CodecType(NoFileCodec())
    object BZipCodec : CodecType(BZip2Codec())
    object GZipCodec : CodecType(GZipCodec())
}
