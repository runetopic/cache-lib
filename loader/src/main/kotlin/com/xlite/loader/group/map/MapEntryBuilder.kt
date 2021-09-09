package com.xlite.loader.group.map

import com.xlite.cache.compression.Compression
import com.xlite.cache.exception.FileNotFoundException
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
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


        return type
    }
}