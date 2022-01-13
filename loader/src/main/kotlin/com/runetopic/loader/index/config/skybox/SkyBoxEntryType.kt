package com.runetopic.loader.index.config.skybox

import com.runetopic.loader.IEntryType

data class SkyBoxEntryType(
    private val id: Int = 0,
    var anInt2392: Int = -1,
    var sphereIds: IntArray? = null,
    var textureId: Int = -1
) : IEntryType {
    override fun getId(): Int = id
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SkyBoxEntryType

        if (id != other.id) return false
        if (anInt2392 != other.anInt2392) return false
        if (sphereIds != null) {
            if (other.sphereIds == null) return false
            if (!sphereIds.contentEquals(other.sphereIds)) return false
        } else if (other.sphereIds != null) return false
        if (textureId != other.textureId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + anInt2392
        result = 31 * result + (sphereIds?.contentHashCode() ?: 0)
        result = 31 * result + textureId
        return result
    }
}
