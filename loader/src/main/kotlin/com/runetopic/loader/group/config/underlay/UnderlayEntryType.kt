package com.runetopic.loader.group.config.underlay

import com.runetopic.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class UnderlayEntryType(
    private val id: Int = 0,
    var color: Int = 0,
    var textureId: Int = -1,
    var textureResolution: Int = 512,
    var aBoolean2647: Boolean = true,
    var aBoolean2648: Boolean = true
): IEntryType {
    override fun getId(): Int = id
}