package com.runetopic.loader.index.map

import com.runetopic.loader.IEntryType
import com.runetopic.loader.util.vector.Vector3f

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class MapEntryType(
    private val id: Int = 0,
    val regionX: Int,
    val regionY: Int,
    val tiles: Array<Array<Array<Tile?>>> = Array(PLANES) { Array(MAP_SIZE) { arrayOfNulls(MAP_SIZE) } },
    var atmosphere: AtmosphereEntryType = AtmosphereEntryType(),
    var cameraAngles: Array<Array<ByteArray>?> = arrayOfNulls(4)
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
        var sunBrightness: Float = 1.1523438f,
        var sunCoordinateX: Float = 0.69921875f,
        var sunCoordinateY: Float = 1.2f,
        var sunAngle: Vector3f = Vector3f(-50f, -60f, -50f),
        var skyColor: Int = 13156520,
        var fogDensity: Int = 0,
        var highDynamicRange: HighDynamicRange = HighDynamicRange(),
        var lightingEntryId: Int = -1,
        var environmentMap: EnvironmentMap = EnvironmentMap(682, 683, 684, 685, 686, 687),
        var lightEffectPoint: LightEffectPoint = LightEffectPoint()
    )

    data class HighDynamicRange(
        var bloom: Float = 1.0f,
        var brightness: Float = 1.0f,
        var whitePoint: Float = 0.25f
    )

    data class LightEffectPoint(
        var fromFirstLevel: Boolean = false,
        var toLastLevel: Boolean = false,
        var level: Int = 0,
        var x: Int = 0,
        var y: Int = 0,
        var z: Int = 0,
        var color: Int = 0,
        var strength: Int = 0,
    )

    data class EnvironmentMap(
        var faceTop: Int,
        var faceBottom: Int,
        var faceFront: Int,
        var faceBack: Int,
        var faceLeft: Int,
        var faceRight: Int
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