package com.xlite.loader.group.config.spotanim

import com.xlite.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class SpotAnimationEntryType(
    private val id: Int = 0,
    var rotation: Int = 0,
    var textureToReplace: ShortArray? = null,
    var textureToFind: ShortArray? = null,
    var resizeY: Int = 128,
    var animationId: Int = -1,
    var colorToFind: ShortArray? = null,
    var colorToReplace: ShortArray? = null,
    var resizeX: Int = 128,
    var modelId: Int = 0,
    var ambient: Int = 0,
    var contrast: Int = 0,
    var anInt2667: Int = -1,
    var aByte2664: Int = 0,
    var aBoolean2678: Boolean = false
) : IEntryType {

    override fun getId(): Int = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpotAnimationEntryType

        if (id != other.id) return false
        if (rotation != other.rotation) return false
        if (textureToReplace != null) {
            if (other.textureToReplace == null) return false
            if (!textureToReplace.contentEquals(other.textureToReplace)) return false
        } else if (other.textureToReplace != null) return false
        if (textureToFind != null) {
            if (other.textureToFind == null) return false
            if (!textureToFind.contentEquals(other.textureToFind)) return false
        } else if (other.textureToFind != null) return false
        if (resizeY != other.resizeY) return false
        if (animationId != other.animationId) return false
        if (colorToFind != null) {
            if (other.colorToFind == null) return false
            if (!colorToFind.contentEquals(other.colorToFind)) return false
        } else if (other.colorToFind != null) return false
        if (colorToReplace != null) {
            if (other.colorToReplace == null) return false
            if (!colorToReplace.contentEquals(other.colorToReplace)) return false
        } else if (other.colorToReplace != null) return false
        if (resizeX != other.resizeX) return false
        if (modelId != other.modelId) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (anInt2667 != other.anInt2667) return false
        if (aByte2664 != other.aByte2664) return false
        if (aBoolean2678 != other.aBoolean2678) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + rotation
        result = 31 * result + (textureToReplace?.contentHashCode() ?: 0)
        result = 31 * result + (textureToFind?.contentHashCode() ?: 0)
        result = 31 * result + resizeY
        result = 31 * result + animationId
        result = 31 * result + (colorToFind?.contentHashCode() ?: 0)
        result = 31 * result + (colorToReplace?.contentHashCode() ?: 0)
        result = 31 * result + resizeX
        result = 31 * result + modelId
        result = 31 * result + ambient
        result = 31 * result + contrast
        result = 31 * result + anInt2667
        result = 31 * result + aByte2664
        result = 31 * result + aBoolean2678.hashCode()
        return result
    }
}