package com.runetopic.loader.index.config.struct

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.*
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class StructEntryBuilder : IEntryBuilder<StructEntryType> {

    lateinit var structTypes: Set<StructEntryType>

    override fun build(store: Js5Store) {
        structTypes = buildSet {
            store.index(2).group(26).files().forEach {
                add(read(it.data.toByteBuffer(), StructEntryType(it.id)))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: StructEntryType): StructEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            249 -> {
                val size = buffer.readUnsignedByte()
                repeat(size) {
                    val string = buffer.readUnsignedByte().toBoolean()
                    type.params[buffer.readUnsignedMedium()] = if (string) buffer.readString() else buffer.int
                }
            }
            else -> throw Exception("Read unused opcode with id: $opcode.")
        } while (true)
        return type
    }
}
