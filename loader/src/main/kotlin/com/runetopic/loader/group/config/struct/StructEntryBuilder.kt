package com.runetopic.loader.group.config.struct

import com.runetopic.cache.extension.readMedium
import com.runetopic.cache.extension.readString
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.toBoolean
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class StructEntryBuilder: IEntryBuilder<StructEntryType> {

    lateinit var structTypes: Set<StructEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        structTypes = buildSet {
            store.index(2).use { group ->
                group.entries(26).forEach {
                    add(read(ByteBuffer.wrap(store.file(group, it.groupId, it.id).data), StructEntryType(it.id)))
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