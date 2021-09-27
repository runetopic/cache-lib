package com.runetopic.cache.compression

import com.runetopic.cache.compression.impl.BZip2Codec
import com.runetopic.cache.compression.impl.GZipCodec
import com.runetopic.cache.compression.impl.NoFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal sealed class CompressionType(
    val codec: IFileCodec
) {
    object BadCompression: CompressionType(NoFileCodec())
    object NoCompression: CompressionType(NoFileCodec())
    object BZipCompression: CompressionType(BZip2Codec())
    object GZipCompression: CompressionType(GZipCodec())
}