package com.xlite.cache.compression

import com.xlite.cache.compression.impl.BZip2Codec
import com.xlite.cache.compression.impl.GZipCodec
import com.xlite.cache.compression.impl.UhandledFileCodec

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
sealed class CompressionType(
    val codec: IFileCodec
) {

    object BadCompression: CompressionType(UhandledFileCodec())
    object NoCompression: CompressionType(UhandledFileCodec())
    object BZipCompression: CompressionType(BZip2Codec())
    object GZipCompression: CompressionType(GZipCodec())
}