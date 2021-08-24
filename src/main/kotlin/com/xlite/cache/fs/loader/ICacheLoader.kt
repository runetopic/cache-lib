package com.xlite.cache.fs.loader

import com.xlite.cache.fs.Index

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
interface ICacheLoader: AutoCloseable {
    fun load()
    fun readReferenceTable(id: Int): ByteArray
    fun readIndex(id: Int): Index
}