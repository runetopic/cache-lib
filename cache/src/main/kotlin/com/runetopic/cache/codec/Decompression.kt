@file:JvmName("Decompression")
package com.runetopic.cache.codec

/**
 * @author Jordan Abraham
 */
fun ByteArray.decompress(keys: IntArray): ByteArray = ContainerCodec.decompress(this, keys).data
fun ByteArray.decompress(): ByteArray = ContainerCodec.decompress(this, intArrayOf()).data
