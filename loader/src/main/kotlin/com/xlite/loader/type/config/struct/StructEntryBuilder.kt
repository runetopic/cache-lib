package com.xlite.loader.type.config.struct

import com.xlite.cache.extension.readMedium
import com.xlite.cache.extension.readString
import com.xlite.cache.extension.readUnsignedByte
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
               val js5File = group.files[26]

                js5File.entries.forEach { fileEntry ->
                    val entry = store.entry(group, 26, fileEntry.id)
                    add(read(ByteBuffer.wrap(entry.data), StructEntryType(fileEntry.id)))
                }
            }
        }
    }

    override fun read(buf: ByteBuffer, type: StructEntryType): StructEntryType {
        do when (val opcode: Int = buf.readUnsignedByte()) {
            0 -> break
            249 -> {
                val length: Int = buf.get().toInt() and 0xff

                for (i in 0 until length) {
                    val isString = buf.get().toInt() and 0xff == 1
                    val key: Long = buf.readMedium().toLong()
                    var value: Any
                    value = if (isString) {
                        buf.readString()
                    } else {
                        buf.int
                    }
                    type.params[key] = value
                }
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}