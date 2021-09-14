package com.runetopic.loader.group.config.mouseicon

import com.runetopic.loader.IEntryType

data class MouseIconEntryType(
    private val id: Int = 0,
    var xCoord: Int = 0,
    var yCoord: Int = 0,
    var spriteId: Int = 0
): IEntryType {
    override fun getId(): Int = id

}