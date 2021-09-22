package com.runetopic.loader.index.config.overlay

import com.runetopic.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class OverlayEntryType(
    private val id: Int = 0,
    var color: Int = 0,
    var textureId: Int = -1,
    var occlude: Boolean = true,
    var secondaryColor: Int = -1,
    var textureResolution: Int = 512,
    var aBoolean397: Boolean = true,
    var anInt398: Int = 8,
    var aBoolean391: Boolean = false,
    var anInt392: Int = 1190717,
    var anInt395: Int = 64,
    var anInt388: Int = 127
): IEntryType {
    override fun getId(): Int = id
}