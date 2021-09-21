package com.runetopic.loader.group.config.inv

import com.runetopic.cache.extension.*
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class InventoryEntryBuilder: IEntryBuilder<InventoryEntryType> {
    lateinit var inventoryTypes: Set<InventoryEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        inventoryTypes = buildSet {
            store.index(2).use { group ->
                group.entries(5).forEach {
                    add(read(ByteBuffer.wrap(store.file(group, it.groupId, it.id).data), InventoryEntryType(it.id)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: InventoryEntryType): InventoryEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            2 -> type.size = buffer.readUnsignedShort()
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}