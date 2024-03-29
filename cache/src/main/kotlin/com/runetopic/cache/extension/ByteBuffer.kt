package com.runetopic.cache.extension

import java.nio.ByteBuffer

internal fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
internal fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
internal fun ByteBuffer.readUnsignedMedium(): Int = ((readUnsignedByte() shl 16) or (readUnsignedByte() shl 8) or readUnsignedByte())
internal fun ByteBuffer.readUnsignedIntShortSmart(): Int = if (get(position()).toInt() < 0) int and Int.MAX_VALUE else readUnsignedShort()