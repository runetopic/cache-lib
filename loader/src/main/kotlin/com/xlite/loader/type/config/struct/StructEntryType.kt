package com.xlite.loader.type.config.struct

import com.xlite.loader.IEntryType

data class StructEntryType(
    private val id: Int = 0,
    var params: HashMap<Long, Any> = hashMapOf()
): IEntryType {
    override fun getId(): Int = id
}