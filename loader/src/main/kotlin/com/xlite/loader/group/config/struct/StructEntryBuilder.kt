package com.xlite.loader.group.config.struct

import com.xlite.cache.extension.readMedium
import com.xlite.cache.extension.readString
import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.toBoolean
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal class StructEntryBuilder: IEntryBuilder<StructEntryType> {

    lateinit var structTypes: Set<StructEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        structTypes = buildSet {
            store.group(2).use { group ->
                group.entries(26).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), StructEntryType(it.entryId)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: StructEntryType): StructEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            249 -> {
                val size = buffer.readUnsignedByte()
                (0 until size).forEach { _ ->
                    val string = buffer.readUnsignedByte().toBoolean()
                    type.params[buffer.readMedium()] = if (string) buffer.readString() else buffer.int
                }
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}