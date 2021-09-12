package com.xlite.loader.group.config.inv

import com.xlite.cache.extension.*
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
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
            store.group(2).use { group ->
                group.entries(5).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), InventoryEntryType(it.entryId)))
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