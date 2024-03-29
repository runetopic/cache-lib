package com.runetopic.loader.index.spotanim

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.readUnsignedShort
import com.runetopic.loader.extension.toByteBuffer
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal class SpotAnimationEntryBuilder: IEntryBuilder<SpotAnimationEntryType> {

    lateinit var spotAnimations: Set<SpotAnimationEntryType>

    override fun build(store: Js5Store) {
        spotAnimations = buildSet {
            val index = store.index(21)
            (0 until index.expand()).forEach {
                index.group(it ushr 8)?.file(it and 0xFF)?.data?.toByteBuffer()?.let { buffer ->
                    read(buffer, SpotAnimationEntryType(it))
                }?.let { type -> add(type) }
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
                val colorToFind = ShortArray(size)
                val colorToReplace = ShortArray(size)
                (0 until size).forEach {
                    colorToFind[it] = (buffer.readUnsignedShort()).toShort()
                    colorToReplace[it] = (buffer.readUnsignedShort()).toShort()
                }
                type.colorToFind = colorToFind
                type.colorToReplace = colorToReplace
            }
            41 -> {
                val size = buffer.readUnsignedByte()
                val textureToFind = ShortArray(size)
                val textureToReplace = ShortArray(size)
                (0 until size).forEach {
                    textureToFind[it] = (buffer.readUnsignedShort()).toShort()
                    textureToReplace[it] = (buffer.readUnsignedShort()).toShort()
                }
                type.textureToFind = textureToFind
                type.textureToReplace = textureToReplace
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}