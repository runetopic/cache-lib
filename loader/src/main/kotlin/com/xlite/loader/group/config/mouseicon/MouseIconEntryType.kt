package com.xlite.loader.group.config.mouseicon

import com.xlite.loader.IEntryType

data class MouseIconEntryType(
    private val id: Int = 0,
    var xCoord: Int = 0,
    var zCoord: Int = 0,
    var spriteId: Int = 0
): IEntryType {
    override fun getId(): Int = id

}