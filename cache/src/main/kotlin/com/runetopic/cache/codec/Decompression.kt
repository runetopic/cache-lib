@file:JvmName("Decompression")
package com.runetopic.cache.codec

/**
 * @author Jordan Abraham
 */
fun ByteArray.decompress(keys: IntArray = intArrayOf()): ByteArray {
    return ContainerCodec.decompress(this, keys).data
}

fun ByteArray.decompress(): ByteArray {
    return ContainerCodec.decompress(this, intArrayOf()).data
}