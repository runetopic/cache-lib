package com.runetopic.loader.group.config.skybox

import com.runetopic.cache.extension.*
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class SkyBoxEntryBuilder: IEntryBuilder<SkyBoxEntryType> {
    lateinit var skyBoxTypes: Set<SkyBoxEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        skyBoxTypes = buildSet {
            store.group(2).use { group ->
                group.entries(29).forEach {
                    add(read(ByteBuffer.wrap(store.entry(group, it.fileId, it.entryId).data), SkyBoxEntryType(it.entryId)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: SkyBoxEntryType): SkyBoxEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.textureId = buffer.readUnsignedShort()
            2 -> {
                val size = buffer.readUnsignedByte()
                val sphereIds = IntArray(size)
                (0 until size).forEach {
                    sphereIds[it] = buffer.readUnsignedShort()
                }
            }
            3 -> type.anInt2392 = buffer.readUnsignedByte()
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}