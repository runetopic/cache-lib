package com.runetopic.loader.index.config.inv

import com.runetopic.loader.IEntryType

data class InventoryEntryType(
    private val id: Int = 0,
    var size: Int = 0
) : IEntryType {
    override fun getId(): Int = id
}
