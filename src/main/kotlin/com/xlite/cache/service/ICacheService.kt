package com.xlite.cache.service

import com.xlite.cache.fs.Index
import com.xlite.cache.fs.file.IDataFile
import com.xlite.cache.fs.file.IIndexFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheService: AutoCloseable {
    fun getMainIndex(): IIndexFile
    fun getData(): IDataFile
    fun readReferenceTable(id: Int): ByteArray
    fun readIndex(id: Int): Index
}