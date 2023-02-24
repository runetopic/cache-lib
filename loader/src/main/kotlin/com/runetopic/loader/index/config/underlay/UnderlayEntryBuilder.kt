package com.runetopic.loader.index.config.underlay

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.readUnsignedMedium
import com.runetopic.loader.extension.readUnsignedShort
import com.runetopic.loader.extension.toByteBuffer
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class UnderlayEntryBuilder: IEntryBuilder<UnderlayEntryType> {

    lateinit var underlays: Set<UnderlayEntryType>

    override fun build(store: Js5Store) {
        underlays = buildSet {
            store.index(2).group(1)?.files()?.forEach {
                add(read(it.data.toByteBuffer(), UnderlayEntryType(it.id)))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: UnderlayEntryType): UnderlayEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.color = buffer.readUnsignedMedium()
            2 -> buffer.readUnsignedShort().let {
                type.textureId = if (it == 65535) -1 else it
            }
            3 -> type.textureResolution = buffer.readUnsignedShort() shl 2
            4 -> type.aBoolean2647 = false
            5 -> type.aBoolean2648 = false
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while(true)
        return type
    }
}