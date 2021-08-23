package com.xlite.cache.service.impl

import com.xlite.cache.constant.FileConstants
import com.xlite.cache.constant.FileConstants.MAIN_FILE_255
import com.xlite.cache.constant.FileConstants.MAIN_INDEX_ID
import com.xlite.cache.fs.file.impl.DataFile
import com.xlite.cache.fs.file.impl.IndexFile
import com.xlite.cache.service.ICacheService
import java.io.File
import java.io.FileNotFoundException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class CacheServiceRS2(private val directory: String): ICacheService {
    private val data = getData()
    private val mainIndex = getMainIndex()

    override fun getMainIndex(): IndexFile {
        val file = File("${directory}${MAIN_FILE_255}")
        if (file.exists().not()) throw FileNotFoundException("Missing $MAIN_FILE_255 in directory $directory")
        return IndexFile(MAIN_INDEX_ID, file)
    }

    override fun getData(): DataFile {
        val file = File("${directory}${FileConstants.MAIN_FILE_DAT}")
        if (file.exists().not()) throw FileNotFoundException("Missing ${FileConstants.MAIN_FILE_DAT} in directory $directory")
        return DataFile(file)
    }

    override fun close() {
        data.close()
        mainIndex.close()
    }
}