package com.xlite.loader.group.config.param

import com.xlite.cache.extension.readUnsignedByte
import com.xlite.cache.extension.readCp1252Char
import com.xlite.cache.extension.readString
import com.xlite.cache.store.Store
import com.xlite.loader.IEntryBuilder
import java.lang.Exception
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
            store.group(2).use { group ->
                group.entries(11).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), ParamEntryType(it.entryId)))
                }
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