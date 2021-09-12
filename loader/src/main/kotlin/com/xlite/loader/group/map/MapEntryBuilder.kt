package com.xlite.loader.group.map

import com.xlite.cache.compression.Compression
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.extension.skip
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import com.xlite.loader.util.vector.Vector3f
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class MapEntryBuilder : IEntryBuilder<MapEntryType> {

    lateinit var mapTypes: Set<MapEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        mapTypes = buildSet {
            store.group(5).use {
                (0 until Short.MAX_VALUE + 1).forEach { regionId ->
                    val regionX: Int = regionId shr 8
                    val regionY: Int = regionId and 0xFF
                    store.file(it, "m${regionX}_${regionY}")?.let { file ->
                        val container = Compression.decompress(file.data!!, emptyArray())
                        add(read(ByteBuffer.wrap(container.data), MapEntryType(regionId, regionX, regionY)))
                    }
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: MapEntryType): MapEntryType {
        (0 until MapEntryType.PLANES).forEach { plane ->
            (0 until MapEntryType.MAP_SIZE).forEach { x ->
                (0 until MapEntryType.MAP_SIZE).forEach { z ->
                    val tile = MapEntryType.Tile()

                    while (true) {
                        when (val opcode = buffer.readUnsignedByte()) {
                            0 -> break
                            1 -> {
                                tile.height = buffer.readUnsignedByte()
                                break
                            }
                            in 0..49 -> {
                                tile.attrOpcode = opcode
                                tile.overlayId = buffer.get()
                                tile.overlayPath = ((opcode - 2) / 4).toByte()
                                tile.overlayRotation = (opcode - 2 and 3).toByte()
                            }
                            in 0..81 -> {
                                tile.settings = (opcode - 49).toByte()
                            }
                            else -> tile.underlayId = (opcode - 81).toByte()
                        }
                    }

                    type.tiles[plane][x][z] = tile
                }
            }
        }

        if (buffer.remaining() > 0) {
            readAtmosphere(buffer, type)
        }
        return type
    }

    private fun readAtmosphere(buffer: ByteBuffer, type: MapEntryType) {
        while (buffer.array().size > buffer.position()) {
            when (buffer.readUnsignedByte()) {
                0 -> {
                    val flag = buffer.readUnsignedByte()
                    if ((flag and 0x1) != 0) type.atmosphere.sunColor = buffer.int
                    if ((flag and 0x2) != 0) type.atmosphere.sunBrightness = buffer.readUnsignedShort() / 256.0f
                    if ((flag and 0x4) != 0) type.atmosphere.sunCoordinateX = buffer.readUnsignedShort() / 256.0f
                    if ((flag and 0x8) != 0) type.atmosphere.sunCoordinateY = buffer.readUnsignedShort() / 256.0f
                    if ((flag and 0x10) != 0) type.atmosphere.sunAngle = Vector3f(buffer.short.toFloat(), buffer.short.toFloat(), buffer.short.toFloat())
                    if ((flag and 0x20) != 0) type.atmosphere.skyColor = buffer.int
                    if ((flag and 0x40) != 0) type.atmosphere.fogDensity = buffer.readUnsignedShort()
                    if ((flag and 0x80) != 0) {
                        val faceTop = buffer.readUnsignedShort()
                        val faceBottom = buffer.readUnsignedShort()
                        val faceFront = buffer.readUnsignedShort()
                        val faceBack = buffer.readUnsignedShort()
                        val faceLeft = buffer.readUnsignedShort()
                        val faceRight = buffer.readUnsignedShort()
                        type.atmosphere.environmentMap = MapEntryType.EnvironmentMap(faceTop, faceBottom, faceFront, faceBack, faceLeft, faceRight)
                    }
                }
                1 -> {
                    val lightCount = buffer.readUnsignedByte()

                    if (lightCount > 0) {
                        (0 until lightCount).forEach { _ ->
                            val flag = buffer.readUnsignedByte()
                            val fromFirstLevel = flag and 0x8 != 0
                            val toLastLevel = flag and 0x10 != 0
                            val level = flag and 0x7

                            val x = buffer.readUnsignedShort() shl 2
                            val z = buffer.readUnsignedShort() shl 2
                            val y = buffer.readUnsignedShort() shl 2
                            val size = buffer.readUnsignedByte() * 2 + 1
                            val ranges = ShortArray(size)

                            (ranges.indices).forEach {
                                val unsigned = buffer.readUnsignedShort()
                                var start = unsigned ushr 8
                                if (size <= start) start = size - 1
                                var end = unsigned and 0xFF
                                if (end > -start + size) end = -start + size
                                ranges[it] = (end or end shl 8).toShort()
                            }
                            val color = buffer.readUnsignedShort()
                            val mask = buffer.readUnsignedByte()
                            val fileId = mask and 0x1f
                            val strength = mask shl 3 and 0x700

                            if (fileId == 31) {
                                val entryId = buffer.readUnsignedShort()
                                type.atmosphere.lightingEntryId = entryId
                            }
                            type.atmosphere.lightEffectPoint = MapEntryType.LightEffectPoint(fromFirstLevel, toLastLevel, level, x, y, z, color, strength)
                        }
                    }
                }
                2 -> {
                    val bloom = (buffer.readUnsignedByte() * 8) / 255.0f
                    val brightness = (buffer.readUnsignedByte() * 8) / 255.0f
                    val whitePoint = (buffer.readUnsignedByte() * 8) / 255.0f
                    type.atmosphere.highDynamicRange = MapEntryType.HighDynamicRange(bloom, brightness, whitePoint)
                }
                128 -> buffer.skip(10)
                129 -> {
                    val cameraAngles = arrayOfNulls<Array<ByteArray>>(4)
                    (0 until 4).forEach { index ->
                        val i = buffer.get().toInt()
                        //0 checks if the array exists.
                        if (i == 1) {
                            val regionParamX = 104
                            val regionParamY = 104
                            cameraAngles[index] = Array(regionParamX + 1) { ByteArray(regionParamY + 1) }
                            (0 until 64 step 4).forEach { x ->
                                (0 until 64 step 4).forEach { z ->
                                    val angle = buffer.get()
                                    // TODO still some bytes in the buffer to be looked at with camera angles. Will come back when we implement a map tool
                                    cameraAngles[index]?.get(x)?.set(z, angle)
                                }
                            }
                        }
                    }
                    type.cameraAngles = cameraAngles
                }
            }
        }
    }
}