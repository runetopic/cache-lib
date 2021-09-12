package com.xlite.loader.group.map

import com.xlite.cache.compression.Compression
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.extension.skip
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import com.xlite.loader.group.lighting.AtmosphereLightingEntryType
import com.xlite.loader.group.lighting.AtmosphereLightingEntryTypeBuilder
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
                    val js5File = store.file(it, "m${regionX}_${regionY}")

                    if (js5File != null) {
                        val container = Compression.decompress(js5File.data!!, emptyArray())
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

        if (buffer.remaining() > 0) readAtmosphere(buffer, type)
        return type
    }

    private fun readAtmosphere(buffer: ByteBuffer, type: MapEntryType) {
        while (buffer.array().size > buffer.position()) {
            when (buffer.readUnsignedByte()) {
                0 -> {
                    val flag = buffer.readUnsignedByte()

                    if ((flag and 0x1) != 0) {
                        type.atmosphere.sunColor = buffer.int
                    }

                    if ((flag and 0x2) != 0) {
                        type.atmosphere.aFloat2882 = buffer.readUnsignedShort() / 256.0f
                    }

                    if ((flag and 0x4) != 0) {
                        type.atmosphere.aFloat2884 = buffer.readUnsignedShort() / 256.0f
                    }

                    if ((flag and 0x8) != 0) {
                        type.atmosphere.aFloat2876 = buffer.readUnsignedShort() / 256.0f
                    }

                    if ((flag and 0x10) != 0) {
                        type.atmosphere.anInt2887 = buffer.short.toInt()
                        type.atmosphere.anInt2886 = buffer.short.toInt()
                        type.atmosphere.anInt2881 = buffer.short.toInt()
                    }

                    if ((flag and 0x20) != 0) {
                        type.atmosphere.anInt2888 = buffer.int
                    }

                    if ((flag and 0x40) != 0) {
                        type.atmosphere.anInt2890 = buffer.readUnsignedShort()
                    }

                    if ((flag and 0x80) != 0) {
                        val i_6_ = buffer.readUnsignedShort()
                        val i_7_ = buffer.readUnsignedShort()
                        val i_8_ = buffer.readUnsignedShort()
                        val i_9_ = buffer.readUnsignedShort()
                        val i_10_ = buffer.readUnsignedShort()
                        val i_11_ = buffer.readUnsignedShort()
                        // TODO: figure out what to do with this fucking shit
                    }
                }
                1 -> {
                    val length = buffer.readUnsignedByte()

                    if (length > 0) {
                        (0 until length).forEach {
                            var anInt1574 = buffer.readUnsignedByte()
                            val aBoolean1573 = anInt1574 and 0x8 != 0
                            val aBoolean1579 = anInt1574 and 0x10 != 0
                            anInt1574 = anInt1574 and 0x7

                            val i_14_ = buffer.readUnsignedShort() shl 2
                            val i_15_ = buffer.readUnsignedShort() shl 2
                            val i_16_ = buffer.readUnsignedShort() shl 2
                            val i_17_ = buffer.readUnsignedByte()
                            val i_18_ = i_17_ * 2 + 1
                            val aShortArray1570 = ShortArray(i_18_)

                            (0 until i_18_).forEach {
                                buffer.readUnsignedShort()
                            }
                            buffer.readUnsignedShort()

                            val unsigned = buffer.readUnsignedByte()
                            val fileId = unsigned and 0x1f

                            if (fileId == 31) {
                                val entryId = buffer.readUnsignedShort()
                                type.atmosphere.lightingEntryId = entryId
                            }
                        }
                    }
                }
                2 -> {
                    type.atmosphere.aFloat2889 = (buffer.readUnsignedByte() * 8) / 255.0f
                    type.atmosphere.aFloat2877 = (buffer.readUnsignedByte() * 8) / 255.0f
                    type.atmosphere.aFloat2880 = (buffer.readUnsignedByte() * 8) / 255.0f
                }
                128 -> {
                    buffer.skip(10)
                }
                129 -> {
                    val aByteArrayArrayArray1561 = arrayOfNulls<Array<ByteArray>>(4)
                    (0 until 4).forEach {
                        val i_60_ = buffer.get().toInt()
                        if (i_60_ != 0/* || (aByteArrayArrayArray1561[])*/) {
                            if (i_60_ == 1) {
                                (0 until 64 step 4).forEach {
                                    (0 until 64 step 4).forEach {
                                        buffer.skip(1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}