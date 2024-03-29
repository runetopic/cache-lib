package com.runetopic.loader.index.config.mouseicon

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.readUnsignedShort
import com.runetopic.loader.extension.toByteBuffer
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class MouseIconEntryBuilder: IEntryBuilder<MouseIconEntryType> {
    lateinit var mouseIconTypes: Set<MouseIconEntryType>

    override fun build(store: Js5Store) {
        mouseIconTypes = buildSet {
            store.index(2).group(33)?.files()?.forEach {
                add(read(it.data.toByteBuffer(), MouseIconEntryType(it.id)))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: MouseIconEntryType): MouseIconEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.spriteId = buffer.readUnsignedShort()
            2 -> {
                type.xCoord = buffer.readUnsignedByte()
                type.yCoord = buffer.readUnsignedByte()
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}