package com.runetopic.cache.extension

internal fun String.nameHash(): Int {
    var hash = 0
    this.forEach { element -> hash = element.toInt() + ((hash shl 5) - hash) }
    return hash
}
