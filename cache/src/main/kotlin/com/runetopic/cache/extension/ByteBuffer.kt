package com.runetopic.cache.extension

import java.nio.ByteBuffer

internal fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
internal fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
internal fun ByteBuffer.readUnsignedIntShortSmart(): Int = if (get(position()).toInt() < 0) int and Int.MAX_VALUE else readUnsignedShort()

internal fun ByteBuffer.remainingBytes(): ByteArray {
    val bytes = ByteArray(remaining())
    get(bytes)
    return bytes
}