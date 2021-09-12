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
    val tiles: Array<Array<Array<Tile?>>> = Array(PLANES) { Array(MAP_SIZE) { arrayOfNulls(MAP_SIZE) } },
    var atmosphere: AtmosphereEntryType = AtmosphereEntryType()
) : IEntryType {
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

    data class AtmosphereEntryType(
        var sunColor: Int = 16777215,
        var aFloat2882: Float = 1.1523438f,
        var aFloat2884: Float = 0.69921875f,
        var aFloat2876: Float = 1.2f,
        var anInt2887: Int = -50,
        var anInt2886: Int = -60,
        var anInt2881: Int = -50,
        var anInt2888: Int = 13156520,
        var anInt2890: Int = 0,
        var aFloat2889: Float = 1.0f,
        var aFloat2880: Float = 1.0f,
        var aFloat2877: Float = 0.25f,
        var lightingEntryId: Int = -1
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