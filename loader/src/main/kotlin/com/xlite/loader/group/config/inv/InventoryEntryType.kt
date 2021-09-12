package com.xlite.loader.group.config.inv

import com.xlite.loader.IEntryType

data class InventoryEntryType(
    private val id: Int = 0,
    var size: Int = 0
): IEntryType {
    override fun getId(): Int = id

}