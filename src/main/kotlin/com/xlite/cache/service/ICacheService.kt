package com.xlite.cache.service

import com.xlite.cache.fs.file.IDataFile
import com.xlite.cache.fs.file.IIndexFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheService: AutoCloseable {
    fun getMainIndex(): IIndexFile
    fun getData(): IDataFile
}