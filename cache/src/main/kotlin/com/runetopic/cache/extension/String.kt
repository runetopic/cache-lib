package com.runetopic.cache.extension

internal fun String.hashed(): Int = fold(0) { hash, char -> char.code + ((hash shl 5) - hash) }
