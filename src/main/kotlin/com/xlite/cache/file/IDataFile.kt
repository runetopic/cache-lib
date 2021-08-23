package com.xlite.cache.file

import java.io.Closeable

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface IDataFile:  Closeable {
    fun read(id: Int, containerId: Int, sector: Int, length: Int): ByteArray
}