package com.runetopic.loader.group.config.lighting

import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class LightingEntryTypeBuilder : IEntryBuilder<LightingEntryType> {

    lateinit var lightings: Set<LightingEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        lightings = buildSet {
            store.group(2).use { group ->
                group.entries(31).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), LightingEntryType(it.entryId)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: LightingEntryType): LightingEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.anInt961 = buffer.readUnsignedByte()
            2-> type.anInt957 = buffer.readUnsignedShort()
            3 -> type.anInt956 = buffer.readUnsignedShort()
            4 -> type.anInt962 = buffer.short.toInt()
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}