package com.runetopic.loader.index.config.param

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readCp1252Char
import com.runetopic.loader.extension.readString
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.toByteBuffer
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class ParamEntryBuilder: IEntryBuilder<ParamEntryType> {

    lateinit var paramTypes: Set<ParamEntryType>

    override fun build(store: Js5Store) {
        paramTypes = buildSet {
            store.index(2).group(11)?.files()?.forEach {
                add(read(it.data.toByteBuffer(), ParamEntryType(it.id)))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: ParamEntryType): ParamEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.identifier = buffer.readCp1252Char()
            2 -> type.defaultInt = buffer.int
            4 -> type.aBoolean1822 = false
            5 -> type.defaultString = buffer.readString()
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}