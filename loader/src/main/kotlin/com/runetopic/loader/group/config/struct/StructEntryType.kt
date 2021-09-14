package com.runetopic.loader.group.config.struct

import com.runetopic.loader.IEntryType

data class StructEntryType(
    private val id: Int = 0,
    var params: MutableMap<Int, Any> = mutableMapOf()
): IEntryType {
    override fun getId(): Int = id
}