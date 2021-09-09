package com.xlite.loader.group.config.spotanim

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
                (0 until group.expandedCapacity()).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it ushr 8, it and 0xFF).data), SpotAnimationEntryType(it)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: SpotAnimationEntryType): SpotAnimationEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.modelId = buffer.readUnsignedShort()
            2 -> type.animationId = buffer.readUnsignedShort()
            4 -> type.resizeX = buffer.readUnsignedShort()
            5 -> type.resizeY = buffer.readUnsignedShort()
            6 -> type.rotation = buffer.readUnsignedShort()
            7 -> type.ambient = buffer.readUnsignedByte()
            8 -> type.contrast = buffer.readUnsignedByte()
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
                type.anInt2667 = buffer.readUnsignedByte() * 256
            }
            15 -> {
                type.aByte2664 = 3
                type.anInt2667 = buffer.readUnsignedShort()
            }
            16 -> {
                type.aByte2664 = 3
                type.anInt2667 = buffer.int
            }
            40 -> {
                val size = buffer.readUnsignedByte()
                type.recolorToFind = ShortArray(size)
                type.recolorToReplace = ShortArray(size)
                (0 until size).forEach {
                    type.recolorToFind!![it] = (buffer.readUnsignedShort()).toShort()
                    type.recolorToReplace!![it] = (buffer.readUnsignedShort()).toShort()
                }
            }
            41 -> {
                val size = buffer.readUnsignedByte()
                type.textureToFind = ShortArray(size)
                type.textureToReplace = ShortArray(size)
                (0 until size).forEach {
                    type.textureToFind!![it] = (buffer.readUnsignedShort()).toShort()
                    type.textureToReplace!![it] = (buffer.readUnsignedShort()).toShort()
                }
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}