package com.xlite.loader.group.config.lighting

import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readUnsignedShort
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class AtmosphereLightingEntryTypeBuilder : IEntryBuilder<AtmosphereLightingEntryType> {

    lateinit var atmosphereTypes: Set<AtmosphereLightingEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        atmosphereTypes = buildSet {
            store.group(2).use { group ->
                group.entries(31).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), AtmosphereLightingEntryType(it.entryId)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: AtmosphereLightingEntryType): AtmosphereLightingEntryType {
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