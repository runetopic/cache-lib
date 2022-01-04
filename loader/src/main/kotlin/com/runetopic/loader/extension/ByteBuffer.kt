package com.runetopic.loader.extension

import java.nio.ByteBuffer
import java.nio.charset.Charset

internal fun ByteBuffer.readCp1252Char(): Char {
    var unsigned = get().toInt() and 0xff
    require(unsigned != 0) { "Non cp1252 character 0x" + unsigned.toString(16) + " provided" }
    if (unsigned in 128..159) {
        var value: Int = cp1252Identifiers[unsigned - 128].code
        if (value == 0) value = 63
        unsigned = value
    }
    return unsigned.toChar()
}

internal fun ByteBuffer.readUnsignedSmart(): Int {
    val peek = get(position()).toInt() and 0xFF
    return if (peek < 128) readUnsignedByte() else (readUnsignedShort()) - 0x8000
}

internal fun ByteBuffer.readUnsignedByte(): Int = get().toInt() and 0xFF
internal fun ByteBuffer.readUnsignedShort(): Int = short.toInt() and 0xFFFF
internal fun ByteBuffer.readUnsignedMedium(): Int = ((readUnsignedByte() shl 16) or (readUnsignedByte() shl 8) or readUnsignedByte())
internal fun ByteBuffer.skip(amount: Int): ByteBuffer = position(position() + amount)

internal fun ByteBuffer.readUnsignedIntSmartShortCompat(): Int {
    var value = 0
    var i = readUnsignedSmart()
    while (i == 32767) {
        i = readUnsignedSmart()
        value += 32767
    }
    value += i
    return value
}

internal fun ByteBuffer.readString(): String {
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

val cp1252Identifiers = charArrayOf(
    '\u20ac',
    '\u0000',
    '\u201a',
    '\u0192',
    '\u201e',
    '\u2026',
    '\u2020',
    '\u2021',
    '\u02c6',
    '\u2030',
    '\u0160',
    '\u2039',
    '\u0152',
    '\u0000',
    '\u017d',
    '\u0000',
    '\u0000',
    '\u2018',
    '\u2019',
    '\u201c',
    '\u201d',
    '\u2022',
    '\u2013',
    '\u2014',
    '\u02dc',
    '\u2122',
    '\u0161',
    '\u203a',
    '\u0153',
    '\u0000',
    '\u017e',
    '\u0178'
)
