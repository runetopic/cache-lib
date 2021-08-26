package com.xlite.cache.definition

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal interface Definition<T> {
    fun decode(id: Int, data: ByteArray): T
}