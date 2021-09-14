package com.runetopic.loader.group.config.mouseicon

import com.runetopic.cache.extension.*
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class MouseIconEntryBuilder: IEntryBuilder<MouseIconEntryType> {
    lateinit var mouseIconTypes: Set<MouseIconEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        mouseIconTypes = buildSet {
            store.group(2).use { group ->
                group.entries(33).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), MouseIconEntryType(it.entryId)))
                }
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