package com.xlite.cache.loader

import com.xlite.cache.Archive
import com.xlite.cache.Index
import com.xlite.cache.ReferenceTable
import com.xlite.cache.fs.file.IDatFile
import com.xlite.cache.fs.file.IIndexFile
import com.xlite.cache.fs.file.impl.IndexFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheLoader: AutoCloseable {
    fun getMasterIndex(): IIndexFile
    fun getDatFile(): IDatFile
    fun getIndexFiles(): List<IndexFile>
    fun readReferenceTable(id: Int): ByteArray
    fun getReferenceTable(id: Int): ReferenceTable
    fun getIndex(id: Int): Index
    fun readArchive(archive: Archive): ByteArray
}