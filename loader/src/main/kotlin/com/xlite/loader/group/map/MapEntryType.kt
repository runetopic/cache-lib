package com.xlite.loader.group.map

import com.xlite.loader.IEntryType

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class MapEntryType(
    private val id: Int = 0,
    val regionX: Int,
    val regionY: Int,
    val tiles: Array<Array<Array<Tile?>>> = Array(PLANES) { Array(MAP_SIZE) { arrayOfNulls(MAP_SIZE) } }
): IEntryType {
    override fun getId(): Int = id

    data class Tile(
        var height: Int? = null,
        var attrOpcode: Int = 0,
        var settings: Byte = 0,
        var overlayId: Byte = 0,
        var overlayPath: Byte = 0,
        var overlayRotation: Byte = 0,
        var underlayId: Byte = 0
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapEntryType

        if (id != other.id) return false
        if (regionX != other.regionX) return false
        if (regionY != other.regionY) return false
        if (!tiles.contentDeepEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + regionX
        result = 31 * result + regionY
        result = 31 * result + tiles.contentDeepHashCode()
        return result
    }

    companion object {
        const val MAP_SIZE = 64
        const val PLANES = 4
    }
}