package com.runetopic.cache.extension

import java.nio.ByteBuffer

internal fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
internal fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
internal fun ByteBuffer.readUnsignedMedium(): Int = ((readUnsignedByte() shl 16) or (readUnsignedByte() shl 8) or readUnsignedByte())
internal fun ByteBuffer.readUnsignedIntShortSmart(): Int = if (get(position()).toInt() < 0) int and Int.MAX_VALUE else readUnsignedShort()
internal fun ByteBuffer.putIntShortSmart(value: Int) {
    val buffer = when {
        value >= Short.MAX_VALUE -> ByteBuffer.allocate(Int.SIZE_BYTES).putInt(value - Int.MAX_VALUE - 1)
        else -> ByteBuffer.allocate(Short.SIZE_BYTES).putShort(if (value >= 0) value.toShort() else Short.MAX_VALUE)
    }
    put(buffer)
}

internal fun ByteBuffer.remainingBytes(): ByteArray {
    val bytes = ByteArray(remaining())
    get(bytes)
    return bytes
}

class Variable {
    companion object {
        internal fun asSizeBytes(
            protocol: Int,
            value: Int
        ): Int {
            return if (protocol >= 7) if (value >= Short.MAX_VALUE) 4 else 2 else 2
        }
    }
}