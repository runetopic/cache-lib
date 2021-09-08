package com.xlite.cache.extension

import com.xlite.cache.crypto.Whirlpool
import java.nio.ByteBuffer
import java.nio.charset.Charset

fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
fun ByteBuffer.readUnsignedByte(offset: Int): Int = get(offset).toInt() and 0xFF
fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
fun ByteBuffer.readUnsignedShort(offset: Int): Int = ((get(offset).toInt() and 0xFF shl 8) or (get(offset + 1).toInt() and 0xFF))
fun ByteBuffer.readMedium(): Int = (get().toInt() and 0xFF) shl 16 or (get().toInt() and 0xFF shl 8) or (get().toInt() and 0xFF)
fun ByteBuffer.readMedium(offset: Int): Int = (get(offset).toInt() and 0xFF) shl 16 or (get(offset + 1).toInt() and 0xFF shl 8) or (get(offset + 2).toInt() and 0xFF)
fun ByteBuffer.readInt(offset: Int): Int = (get(offset).toInt() and 0xFF) shl 24 or (get(offset + 1).toInt() and 0xFF) shl 16 or (get(offset + 2).toInt() and 0xFF shl 8) or (get(offset + 3).toInt() and 0xFF)
fun ByteBuffer.readBigSmart(): Int = if (peek() >= 0) readUnsignedShort() and 0xFFFF else int and Int.MAX_VALUE
fun ByteBuffer.peek(): Byte = get(position())

fun ByteBuffer.whirlpool(): ByteArray {
    val pos = position()
    val data = ByteArray(limit())
    get(data)
    position(pos)

    return Whirlpool.digest(data)
}


fun ByteBuffer.readString(): String {
    val mark = position()
    var length = 0
    while (get().toInt() != 0) {
        length++
    }
    if (length == 0) return ""
    val byteArray = ByteArray(length)
    position(mark)
    get(byteArray)
    position(position() + 1)
    return String(byteArray, Charset.defaultCharset())
}

fun ByteArray.encipherXTEA(keys: Array<Int>): ByteArray {
    if (keys.isEmpty()) return this
    val buf = ByteBuffer.wrap(this, 0, size)
    val out = ByteBuffer.allocate(size)
    val numBlocks = size / 8
    for (block in 0 until numBlocks) {
        var v0: Int = buf.readInt(0)
        var v1: Int = buf.readInt(4)
        var sum = 0
        for (i in 0 until 32) {
            v0 += (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + keys[sum and 3]
            sum += -0x61c8864
            v1 += (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + keys[sum ushr 11 and 3]
        }
        out.putInt(v0)
        out.putInt(v1)
    }
    out.put(buf)
    return out.array()
}

fun ByteArray.decipherXTEA(keys: Array<Int>): ByteArray {
    if (keys.isEmpty()) return this
    val buf = ByteBuffer.wrap(this, 0, size)
    val out = ByteBuffer.allocate(size)
    val numBlocks = size / 8
    for (block in 0 until numBlocks) {
        var v0: Int = buf.readInt(0)
        var v1: Int = buf.readInt(4)
        var sum = -0x61c88647 * 32
        for (i in 0 until 32) {
            v1 -= (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + keys[sum ushr 11 and 3]
            sum -= -0x61c88647
            v0 -= (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + keys[sum and 3]
        }
        out.putInt(v0)
        out.putInt(v1)
    }
    out.put(buf)
    return out.array()
}

fun ByteBuffer.remainingBytes(): ByteArray {
    val b = ByteArray(remaining())
    get(b)
    return b
}