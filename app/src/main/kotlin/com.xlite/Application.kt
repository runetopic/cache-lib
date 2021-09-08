package com.xlite

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.store.Store
import com.xlite.loader.structs
import java.io.File

fun main() {
    val logger = InlineLogger()
    val store = Store(File("./data/cache/"))
    structs().load(store)
    for (structEntryType in structs().collect()) {
        logger.debug { structEntryType }
    }
}