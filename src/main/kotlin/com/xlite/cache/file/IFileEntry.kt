package com.xlite.cache.file

import com.xlite.cache.Archive

/**
 * @author Jordan Abraham
 */
interface IFileEntry {
    fun decode(id: Int, data: ByteArray, archive: Archive): ByteArray
    fun encode(): ByteArray
}