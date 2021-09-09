package com.xlite.cache.compression

import com.xlite.cache.compression.impl.BZip2Codec
import com.xlite.cache.compression.impl.GZipCodec
import com.xlite.cache.compression.impl.NoFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal sealed class CompressionType(
    val codec: IFileCodec
) {
    object BadCompression: CompressionType(NoFileCodec())
    object NoCompression: CompressionType(NoFileCodec())
    object BZipCompression: CompressionType(BZip2Codec())
    object GZipCompression: CompressionType(GZipCodec())
}