package com.runetopic.loader.index.config.struct

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readMedium
import com.runetopic.loader.extension.readString
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.toBoolean
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class StructEntryBuilder: IEntryBuilder<StructEntryType> {

    lateinit var structTypes: Set<StructEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        structTypes = buildSet {
            store.index(2).getGroup(26).getFiles().forEach {
                add(read(ByteBuffer.wrap(it.getData()), StructEntryType(it.getId())))
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