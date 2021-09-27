package com.runetopic.loader.index.config.skybox

import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
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
            store.index(2).getGroup(29).getFiles().forEach {
                add(read(ByteBuffer.wrap(it.getData()), SkyBoxEntryType(it.getId())))
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