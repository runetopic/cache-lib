package com.xlite.loader.group.config.idk

import com.xlite.loader.IEntryType

data class IdentityKitEntryType(
    private val id: Int = 0,
    var models: IntArray? = null,
    var colorsToFind: ShortArray? = null,
    var colorsToReplace: ShortArray? = null,
    var texturesToFind: ShortArray? = null,
    var texturesToReplace: ShortArray? = null,
    var chatHeadModels: IntArray = intArrayOf(-1, -1, -1, -1, -1)
): IEntryType {
    override fun getId(): Int = id
}