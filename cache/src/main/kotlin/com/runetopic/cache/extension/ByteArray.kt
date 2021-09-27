package com.runetopic.cache.extension

import com.runetopic.cache.compression.Compression

/**
 * @author Jordan Abraham
 */
fun ByteArray.decompress(keys: IntArray = intArrayOf()): ByteArray {
    return Compression.decompress(this, keys).data
}