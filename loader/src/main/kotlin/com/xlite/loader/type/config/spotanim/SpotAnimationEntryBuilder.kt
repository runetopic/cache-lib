package com.xlite.loader.type.config.spotanim

import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal class SpotAnimationEntryBuilder: IEntryBuilder<SpotAnimationEntryType> {

    lateinit var spotAnimations: Set<SpotAnimationEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        spotAnimations = buildSet {
            store.group(21).use { group ->
                (0 until group.capacity()).forEach {
                    store.entry(group, it ushr 8, it and 0xFF)?.let { entry ->
                        add(read(ByteBuffer.wrap(entry.data), SpotAnimationEntryType(it)))
                    }
                }
            }
        }
    }

    override fun read(buf: ByteBuffer, type: SpotAnimationEntryType): SpotAnimationEntryType {
        do when (val opcode: Int = buf.readUnsignedByte()) {
            0 -> break
            1 -> type.modelId = buf.readUnsignedShort()
            2 -> type.animationId = buf.readUnsignedShort()
            4 -> type.resizeX = buf.readUnsignedShort()
            5 -> type.resizeY = buf.readUnsignedShort()
            6 -> type.rotation = buf.readUnsignedShort()
            7 -> type.ambient = buf.readUnsignedByte()
            8 -> type.contrast = buf.readUnsignedByte()
            9 -> {
                type.anInt2667 = 8224
                type.aByte2664 = 3
            }
            10 -> type.aBoolean2678 = true
            11 -> type.aByte2664 = 1
            12 -> type.aByte2664 = 4
            13 -> type.aByte2664 = 5
            14 -> {
                type.aByte2664 = 2
                type.anInt2667 = buf.readUnsignedByte() * 256
            }
            15 -> {
                type.aByte2664 = 3
                type.anInt2667 = buf.readUnsignedShort()
            }
            16 -> {
                type.aByte2664 = 3
                type.anInt2667 = buf.int
            }
            40 -> {
                val size = buf.readUnsignedByte()
                type.recolorToFind = ShortArray(size)
                type.recolorToReplace = ShortArray(size)
                (0 until size).forEach {
                    type.recolorToFind!![it] = (buf.readUnsignedShort()).toShort()
                    type.recolorToReplace!![it] = (buf.readUnsignedShort()).toShort()
                }
            }
            41 -> {
                val size = buf.readUnsignedByte()
                type.textureToFind = ShortArray(size)
                type.textureToReplace = ShortArray(size)
                (0 until size).forEach {
                    type.textureToFind!![it] = (buf.readUnsignedShort()).toShort()
                    type.textureToReplace!![it] = (buf.readUnsignedShort()).toShort()
                }
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}