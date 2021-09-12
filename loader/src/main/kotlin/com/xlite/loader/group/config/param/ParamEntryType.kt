package com.xlite.loader.group.config.param

import com.xlite.loader.IEntryType

data class ParamEntryType(
    private val id: Int = 0,
    var identifier: Char? = null,
    var aBoolean1822: Boolean = true,
    var defaultString: String = "",
    var defaultInt: Int = 0
): IEntryType {
    override fun getId(): Int = id
}