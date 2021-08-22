package com.xlite.cache

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.extension.inject
import com.xlite.cache.file.impl.Index
import com.xlite.cache.file.impl.MainIndex
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Cache {
    init {
        startKoin { modules(module { single { InlineLogger() } }) }
    }

    private val cachedIndices = arrayListOf<Index>()
    private val logger by inject<InlineLogger>()

    fun load(cacheLocation: String) {
        loadMainFile(cacheLocation)
        val mainIndex = loadMain255(cacheLocation)
        cacheIndices(mainIndex, cacheLocation)
    }

    private fun cacheIndices(mainIndex: MainIndex, cacheLocation: String) {
        for (i in 0 until mainIndex.length()) {
            val indexFile = RandomAccessFile("${cacheLocation}main_file_cache.idx$i", "rw")
            val index = Index(indexFile)
            index.decode()
            cachedIndices.add(index)
        }
    }

    private fun loadMain255(cacheLocation: String): MainIndex {
        val file = File("${cacheLocation}${MAIN_FILE_255}")

        if (file.exists().not()) {
            throw FileNotFoundException("Missing or could not find $MAIN_FILE_255 file on path $cacheLocation.")
        }

        val mainIndex = MainIndex(RandomAccessFile(file, "rw"))
        logger.debug { "Found a total of ${mainIndex.length()} indices." }
        mainIndex.decode()
        return mainIndex
    }

    private fun loadMainFile(cacheLocation: String) {
        val mainFile = File("${cacheLocation}${MAIN_FILE}")

        if (mainFile.exists().not()) {
            throw FileNotFoundException("Missing or could not find $MAIN_FILE file on path $cacheLocation.")
        }
    }

    fun openIndex(id: Int): Index {
        return cachedIndices[id]
    }

    private companion object {
        private const val PREFIX = "main_file_cache"
        private const val MAIN_FILE = "${PREFIX}.dat2"
        private const val MAIN_FILE_255 = "${PREFIX}.idx255"
    }
}