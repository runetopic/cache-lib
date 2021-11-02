package com.runetopic.loader.index.config.lighting

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
internal class LightingEntryBuilder : IEntryBuilder<LightingEntryType> {

    lateinit var lightings: Set<LightingEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        lightings = buildSet {
            store.index(2).group(31).files().forEach {
                add(read(it.data.toByteBuffer(), LightingEntryType(it.id)))
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