package com.xlite.loader.group.config.underlay

import com.xlite.cache.extension.readMedium
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class UnderlayEntryBuilder: IEntryBuilder<UnderlayEntryType> {

    lateinit var underlays: Set<UnderlayEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        underlays = buildSet {
            store.group(2).use { group ->
                group.entries(1).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), UnderlayEntryType(it.entryId)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: UnderlayEntryType): UnderlayEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.color = buffer.readMedium()
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