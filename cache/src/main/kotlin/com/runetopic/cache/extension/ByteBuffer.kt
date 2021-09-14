package com.runetopic.cache.extension

import com.runetopic.cache.crypto.Whirlpool
import com.runetopic.cache.store.Constants.cp1252Identifiers
import java.nio.ByteBuffer
import java.nio.charset.Charset

fun ByteBuffer.readCp1252Char(): Char {
    var unsigned = get().toInt() and 0xff
    require(unsigned != 0) { "Non cp1252 character 0x" + unsigned.toString(16) + " provided" }
    if (unsigned in 128..159) {
        var value: Int = cp1252Identifiers[unsigned - 128].toInt()
        if (value == 0) value = 63
        unsigned = value
    }
    return unsigned.toChar()
}

fun ByteBuffer.readUnsignedSmart(): Int {
    val peek: Int = get(position()).toInt() and 0xFF
    return if (peek < 128) readUnsignedByte() else (readUnsignedShort()) - 0x8000
}

fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
fun ByteBuffer.readMedium(): Int = (get().toInt() and 0xFF) shl 16 or (get().toInt() and 0xFF shl 8) or (get().toInt() and 0xFF)
fun ByteBuffer.skip(amount: Int): ByteBuffer = position(position() + amount)

fun ByteBuffer.readUnsignedIntSmartShortCompat(): Int {
    var i = 0
    var i_33_: Int = readUnsignedSmart()
    while (i_33_ == 32767) {
        i_33_ = readUnsignedSmart()
        i += 32767
    }
    i += i_33_
    return i
}

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
    val buffer = ByteBuffer.wrap(this, 0, size)
    val out = ByteBuffer.allocate(size)
    val numBlocks = size / 8
    for (block in 0 until numBlocks) {
        var v0: Int = buffer.int
        var v1: Int = buffer.int
        var sum = 0
        for (i in 0 until 32) {
            v0 += (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + keys[sum and 3]
            sum += -0x61c8864
            v1 += (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + keys[sum ushr 11 and 3]
        }
        out.putInt(v0)
        out.putInt(v1)
    }
    out.put(buffer)
    return out.array()
}

fun ByteArray.decipherXTEA(keys: Array<Int>): ByteArray {
    if (keys.isEmpty()) return this
    val buffer = ByteBuffer.wrap(this, 0, size)
    val out = ByteBuffer.allocate(size)
    val numBlocks = size / 8
    for (block in 0 until numBlocks) {
        var v0: Int = buffer.int
        var v1: Int = buffer.int
        var sum = -0x61c88647 * 32
        for (i in 0 until 32) {
            v1 -= (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + keys[sum ushr 11 and 3]
            sum -= -0x61c88647
            v0 -= (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + keys[sum and 3]
        }
        out.putInt(v0)
        out.putInt(v1)
    }
    out.put(buffer)
    return out.array()
}

fun ByteBuffer.remainingBytes(): ByteArray {
    val b = ByteArray(remaining())
    get(b)
    return b
}