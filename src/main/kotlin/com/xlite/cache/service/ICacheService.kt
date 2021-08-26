package com.xlite.cache.service

import com.xlite.cache.fs.Archive
import com.xlite.cache.fs.Index
import com.xlite.cache.fs.file.IDataFile
import com.xlite.cache.fs.file.IIndexFile
import com.xlite.cache.fs.file.impl.IndexFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheService: AutoCloseable {
    fun getMainIndex(): IIndexFile
    fun getData(): IDataFile
    fun getIndexFiles(): List<IndexFile>
    fun readReferenceTable(id: Int): ByteArray
    fun readIndex(id: Int): Index
    fun readArchive(archive: Archive): ByteArray
}