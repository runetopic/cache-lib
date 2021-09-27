package com.runetopic.loader.index.config.overlay

import com.runetopic.cache.extension.readMedium
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class OverlayEntryBuilder: IEntryBuilder<OverlayEntryType> {

    lateinit var overlays: Set<OverlayEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        overlays = buildSet {
            store.index(2).getGroup(4).getFiles().forEach {
                add(read(ByteBuffer.wrap(it.getData()), OverlayEntryType(it.getId())))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: OverlayEntryType): OverlayEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.color = buffer.readMedium()
            2 -> type.textureId = buffer.readUnsignedByte()
            3 -> buffer.readUnsignedShort().let {
                type.textureId = if (it == 65535) -1 else it
            }
            5 -> type.occlude = false
            7 -> type.secondaryColor = buffer.readMedium()
            8 -> {
                //Some sort of client usage happens here.
            }
            9 -> type.textureResolution = buffer.readUnsignedShort() shl 2
            10 -> type.aBoolean397 = false
            11 -> type.anInt398 = buffer.readUnsignedByte()
            12 -> type.aBoolean391 = true
            13 -> type.anInt392 = buffer.readMedium()
            14 -> type.anInt395 = buffer.readUnsignedByte() shl 2
            16 -> type.anInt388 = buffer.readUnsignedByte()
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}