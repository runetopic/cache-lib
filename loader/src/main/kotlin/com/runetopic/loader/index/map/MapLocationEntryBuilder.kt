package com.runetopic.loader.index.map

import com.runetopic.cache.codec.decompress
import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.readUnsignedIntSmartShortCompat
import com.runetopic.loader.extension.readUnsignedSmart
import java.nio.ByteBuffer
import java.util.zip.ZipException

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class MapLocationEntryBuilder : IEntryBuilder<MapLocationEntryType> {

    lateinit var mapTypes: Set<MapLocationEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        mapTypes = buildSet {
            store.index(5).use {
                (0..Short.MAX_VALUE).forEach { regionId ->
                    val regionX: Int = regionId shr 8
                    val regionY: Int = regionId and 0xFF
                    it.getGroup("l${regionX}_${regionY}").getData().let { data ->
                        if (data.isEmpty()) return@forEach
                        try {
                            add(read(ByteBuffer.wrap(data.decompress()), MapLocationEntryType(regionId, regionX, regionY)))
                        } catch (exception: ZipException) {
                            println("Couldn't decompress Region $regionId")
                        }
                    }
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: MapLocationEntryType): MapLocationEntryType {
        var id = -1
        var idOffset: Int

        while (buffer.readUnsignedIntSmartShortCompat().also { idOffset = it } != 0) {
            id += idOffset

            var position = 0
            var positionOffset: Int

            while (buffer.readUnsignedSmart().also { positionOffset = it } != 0) {
                position += positionOffset - 1
                val localY = position and 0x3F
                val localX = position shr 6 and 0x3F
                val height = position shr 12 and 0x3
                val attributes: Int = buffer.readUnsignedByte()
                val locType = attributes shr 2
                val orientation = attributes and 0x3
                type.locations.add(
                    MapLocationEntryType.MapLocation(
                        id,
                        locType,
                        orientation,
                        localX, localY,
                        height
                    )
                )
            }
        }

        return type
    }
}