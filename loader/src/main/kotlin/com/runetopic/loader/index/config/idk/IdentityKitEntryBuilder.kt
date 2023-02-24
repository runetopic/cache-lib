package com.runetopic.loader.index.config.idk

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.readUnsignedByte
import com.runetopic.loader.extension.readUnsignedShort
import com.runetopic.loader.extension.skip
import com.runetopic.loader.extension.toByteBuffer
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class IdentityKitEntryBuilder: IEntryBuilder<IdentityKitEntryType> {

    lateinit var identityKitTypes: Set<IdentityKitEntryType>

    override fun build(store: Js5Store) {
        identityKitTypes = buildSet {
            store.index(2).group(3)?.files()?.forEach {
                add(read(it.data.toByteBuffer(), IdentityKitEntryType(it.id)))
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: IdentityKitEntryType): IdentityKitEntryType {
        do when (val opcode = buffer.readUnsignedByte()) {
            0 -> break
            1 -> buffer.skip(1)
            2 -> {
                val size = buffer.readUnsignedByte()
                val models = IntArray(size)

                (0 until size).forEach {
                    models[it] = buffer.readUnsignedShort()
                }
                type.models = models
            }
            3 -> {
               // This is no longer used in higher revisions. OSRS uses this
            }
            40 -> {
                val size = buffer.readUnsignedByte()
                val colorsToFind = ShortArray(size)
                val colorsToReplace = ShortArray(size)
                (0 until size).forEach {
                    colorsToFind[it] = buffer.readUnsignedShort().toShort()
                    colorsToReplace[it] = buffer.readUnsignedShort().toShort()
                }
                type.colorsToFind = colorsToFind
                type.colorsToReplace = colorsToReplace
            }
            41 -> {
                val size = buffer.readUnsignedByte()
                val texturesToFind = ShortArray(size)
                val texturesToReplace = ShortArray(size)
                (0 until size).forEach {
                    texturesToFind[it] = buffer.readUnsignedShort().toShort()
                    texturesToReplace[it] = buffer.readUnsignedShort().toShort()
                }
                type.texturesToFind = texturesToFind
                type.texturesToReplace = texturesToReplace
            }
            in 60..69 -> buffer.readUnsignedShort().let { type.chatHeadModels[opcode - 60] = it }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}