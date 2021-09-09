package com.xlite.loader.group.config.struct

import com.xlite.loader.IEntryType

data class StructEntryType(
    private val id: Int = 0,
    var params: MutableMap<Int, Any> = mutableMapOf()
): IEntryType {
    override fun getId(): Int = id
}