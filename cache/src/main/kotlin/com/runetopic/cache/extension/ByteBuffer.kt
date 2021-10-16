package com.runetopic.cache.extension

import com.runetopic.cache.store.Constants.cp1252Identifiers
import com.runetopic.cryptography.toWhirlpool
import java.nio.ByteBuffer
import java.nio.charset.Charset

fun ByteBuffer.readCp1252Char(): Char {
    var unsigned = get().toInt() and 0xff
    require(unsigned != 0) { "Non cp1252 character 0x" + unsigned.toString(16) + " provided" }
    if (unsigned in 128..159) {
        var value: Int = cp1252Identifiers[unsigned - 128].code
        if (value == 0) value = 63
        unsigned = value
    }
    return unsigned.toChar()
}

fun ByteBuffer.readUnsignedSmart(): Int {
    val peek = get(position()).toInt() and 0xFF
    return if (peek < 128) readUnsignedByte() else (readUnsignedShort()) - 0x8000
}

fun ByteBuffer.readUnsignedSmartShort(): Int {
    return if (get(position()).toInt() < 0) int and Int.MAX_VALUE else readUnsignedShort()
}

fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
fun ByteBuffer.readMedium(): Int = (get().toInt() and 0xFF) shl 16 or (get().toInt() and 0xFF shl 8) or (get().toInt() and 0xFF)
fun ByteBuffer.skip(amount: Int): ByteBuffer = position(position() + amount)

fun ByteBuffer.readUnsignedIntSmartShortCompat(): Int {
    var value = 0
    var i = readUnsignedSmart()
    while (i == 32767) {
        i = readUnsignedSmart()
        value += 32767
    }
    value += i
    return value
}

fun ByteBuffer.whirlpool(): ByteArray {
    val position = position()
    val data = ByteArray(limit())
    get(data)
    position(position)
    return data.toWhirlpool()
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

fun ByteBuffer.remainingBytes(): ByteArray {
    val b = ByteArray(remaining())
    get(b)
    return b
}