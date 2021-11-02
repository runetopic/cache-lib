package com.runetopic.loader.index.config.inv

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
internal class InventoryEntryBuilder: IEntryBuilder<InventoryEntryType> {
    lateinit var inventoryTypes: Set<InventoryEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        inventoryTypes = buildSet {
            store.index(2).group(5).files().forEach {
                add(read(it.data.toByteBuffer(), InventoryEntryType(it.id)))
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