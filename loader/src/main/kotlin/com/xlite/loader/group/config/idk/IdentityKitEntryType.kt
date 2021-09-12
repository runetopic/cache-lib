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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdentityKitEntryType

        if (id != other.id) return false
        if (models != null) {
            if (other.models == null) return false
            if (!models.contentEquals(other.models)) return false
        } else if (other.models != null) return false
        if (colorsToFind != null) {
            if (other.colorsToFind == null) return false
            if (!colorsToFind.contentEquals(other.colorsToFind)) return false
        } else if (other.colorsToFind != null) return false
        if (colorsToReplace != null) {
            if (other.colorsToReplace == null) return false
            if (!colorsToReplace.contentEquals(other.colorsToReplace)) return false
        } else if (other.colorsToReplace != null) return false
        if (texturesToFind != null) {
            if (other.texturesToFind == null) return false
            if (!texturesToFind.contentEquals(other.texturesToFind)) return false
        } else if (other.texturesToFind != null) return false
        if (texturesToReplace != null) {
            if (other.texturesToReplace == null) return false
            if (!texturesToReplace.contentEquals(other.texturesToReplace)) return false
        } else if (other.texturesToReplace != null) return false
        if (!chatHeadModels.contentEquals(other.chatHeadModels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (models?.contentHashCode() ?: 0)
        result = 31 * result + (colorsToFind?.contentHashCode() ?: 0)
        result = 31 * result + (colorsToReplace?.contentHashCode() ?: 0)
        result = 31 * result + (texturesToFind?.contentHashCode() ?: 0)
        result = 31 * result + (texturesToReplace?.contentHashCode() ?: 0)
        result = 31 * result + chatHeadModels.contentHashCode()
        return result
    }
}