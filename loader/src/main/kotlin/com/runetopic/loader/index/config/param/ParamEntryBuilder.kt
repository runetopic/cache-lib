package com.runetopic.loader.index.config.param

import com.runetopic.cache.extension.readCp1252Char
import com.runetopic.cache.extension.readString
import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class ParamEntryBuilder: IEntryBuilder<ParamEntryType> {

    lateinit var paramTypes: Set<ParamEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        paramTypes = buildSet {
            store.index(2).getGroup(11).getFiles().forEach {
                add(read(ByteBuffer.wrap(it.value.getData()), ParamEntryType(it.value.getId())))
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