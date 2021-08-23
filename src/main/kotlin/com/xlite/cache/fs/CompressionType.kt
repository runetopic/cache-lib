package com.xlite.cache.fs

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
enum class CompressionType(val opcode: Int) {
    NONE(0),
    BZ2(1),
    GZ(2)
}