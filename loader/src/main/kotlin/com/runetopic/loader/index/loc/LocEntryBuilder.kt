package com.runetopic.loader.index.loc

import com.runetopic.cache.extension.*
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class LocEntryBuilder : IEntryBuilder<LocEntryType> {

    lateinit var mapTypes: Set<LocEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        mapTypes = buildSet {
            store.index(16).use { index ->
                (0 until index.expand()).forEach {
                    add(read(ByteBuffer.wrap(index.getGroup(it ushr 8).getFile(it and 0xFF).getData()), LocEntryType(it)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: LocEntryType): LocEntryType {
        do when (val opcode: Int = buffer.readUnsignedByte()) {
            0 -> break
            1, 5 -> {
                val size = buffer.readUnsignedByte()
                val types = ByteArray(size)
                val models = Array(size) { intArrayOf() }

                (0 until size).forEach {
                    types[it] = buffer.get()
                    val count = buffer.readUnsignedByte()
                    models[it] = IntArray(count)
                    (0 until count).forEach { it2 ->
                        models[it][it2] = buffer.readUnsignedShort()
                    }
                }
                if (opcode == 5) {
                    skipReadModelIds(buffer)
                }

                type.models = models
                type.types = types
            }
            2 -> type.name = buffer.readString()
            14 -> type.sizeX = buffer.readUnsignedByte()
            15 -> type.sizeZ = buffer.readUnsignedByte()
            17 -> {
                type.interactionType = 0
                type.shouldBlockProjectiles = false
            }
            18 -> type.shouldBlockProjectiles = false
            19 -> type.wallOrDoor = buffer.readUnsignedByte()
            21 -> type.contouredGround = 1
            22 -> type.shouldMergeNormals = true
            23 -> type.anInt1146 = 1
            24 -> buffer.readUnsignedShort().let {
                type.animationId = if (it == 65535) -1 else it
            }
            27 -> type.interactionType = 1
            28 -> type.decorDisplacement = (buffer.readUnsignedByte() shl 2)
            29 -> type.ambient = buffer.get().toInt()
            39 -> type.contrast = buffer.get() * 5
            in 30..34 -> buffer.readString().let { type.actions[opcode - 30] = it }
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
            42 -> {
                val length = buffer.readUnsignedByte()
                val aByteArray1118 = ByteArray(length)

                (0 until length).forEach {
                    aByteArray1118[it] = buffer.get()
                }
                type.aByteArray1118 = aByteArray1118
            }
            62 -> type.isRotated = true
            64 -> type.shadowed = false
            65 -> type.modelSizeX = buffer.readUnsignedShort()
            66 -> type.modelSizeHeight = buffer.readUnsignedShort()
            67 -> type.modelSizeY = buffer.readUnsignedShort()
            69 -> type.clippingFlag = buffer.readUnsignedByte()
            70 -> type.anInt1150 = (buffer.readUnsignedShort() shl 2)
            71 -> type.anInt1149 = (buffer.readUnsignedShort() shl 2)
            72 -> type.anInt1124 = (buffer.readUnsignedShort() shl 2)
            73 -> type.aBoolean1131 = true
            74 -> type.aBoolean1157 = true
            75 -> type.anInt1166 = buffer.readUnsignedByte()
            77, 92 -> {
                buffer.readUnsignedShort().let {
                    type.varbitId = if (it == 65535) -1 else it
                }

                buffer.readUnsignedShort().let {
                    type.varpId = if (it == 65535) -1 else it
                }

                var value = -1

                if (opcode == 92) {
                    buffer.readUnsignedShort().let {
                        value = if (it == 65535) -1 else it
                    }
                }

                val size = buffer.readUnsignedByte()
                val configChangeDest = IntArray(size + 2)

                (0..size).forEach { index ->
                    buffer.readUnsignedShort().let {
                        if (it == 65535) configChangeDest[index] = -1
                        else configChangeDest[index] = it
                    }
                }

                configChangeDest[size + 1] = value
                type.configChangeDest = configChangeDest
            }
            78 -> {
                type.anInt1132 = buffer.readUnsignedShort()
                type.anInt1144 = buffer.readUnsignedByte()
            }
            79 -> {
                type.anInt1145 = buffer.readUnsignedShort()
                type.anInt1139 = buffer.readUnsignedShort()
                type.anInt1144 = buffer.readUnsignedByte()
                val length = buffer.readUnsignedByte()
                val anIntArray1127 = IntArray(length)

                (0 until length).forEach { index ->
                    anIntArray1127[index] = buffer.readUnsignedShort()
                }

                type.anIntArray1127 = anIntArray1127
            }
            81 -> {
                type.contouredGround = 2.toByte()
                type.anInt1142 = buffer.readUnsignedByte() * 256
            }
            82 -> type.aBoolean1108 = true
            88 -> type.aBoolean1151 = true
            89 -> type.aBoolean1103 = true
            91 -> type.aBoolean1171 = true
            93 -> {
                type.contouredGround = 5.toByte()
                type.anInt1142 = buffer.readUnsignedShort()
            }
            94 -> {
                type.contouredGround = 4.toByte()
            }
            95 -> {
                type.contouredGround = 5.toByte()
                type.anInt1142 = buffer.short.toInt()
            }
            97 -> type.aBoolean1104 = true
            98 -> type.aBoolean1106 = true
            99 -> {
                type.anInt1184 = buffer.readUnsignedByte()
                type.anInt1173 = buffer.readUnsignedShort()
            }
            100 -> {
                type.anInt1183 = buffer.readUnsignedByte()
                type.anInt1121 = buffer.readUnsignedShort()
            }
            101 -> type.anInt1181 = buffer.readUnsignedByte()
            102 -> type.anInt1160 = buffer.readUnsignedShort()
            103 -> type.anInt1146 = 0
            104 -> type.anInt1136 = buffer.readUnsignedByte()
            105 -> type.aBoolean1148 = true
            106 -> {
                val length = buffer.readUnsignedByte()
                val anIntArray1170 = IntArray(length)
                val anIntArray1154 = IntArray(length)
                (0 until length).forEach { index ->
                    anIntArray1170[index] = buffer.readUnsignedShort()
                    val size = buffer.readUnsignedByte()
                    anIntArray1154[index] = size
                    type.anInt1116 += size
                }

                type.anIntArray1170 = anIntArray1170
                type.anIntArray1154 = anIntArray1154
            }
            107 -> type.anInt1101 = buffer.readUnsignedShort()
            in 150..154 -> buffer.readString().let { type.actions[opcode -150] = it }
            160 -> {
                val length = buffer.readUnsignedByte()
                val anIntArray1153 = IntArray(length)
                (0 until length).forEach { index ->
                    anIntArray1153[index] = buffer.readUnsignedShort()
                }
                type.anIntArray1153 = anIntArray1153
            }
            162 -> {
                type.contouredGround = 3.toByte()
                type.anInt1142 = buffer.int
            }
            163 -> {
                type.aByte1123 = buffer.get()
                type.aByte1110 = buffer.get()
                type.aByte1169 = buffer.get()
                type.aByte1109 = buffer.get()
            }
            164 -> type.anInt1112 = buffer.short.toInt()
            165 -> type.anInt1115 = buffer.short.toInt()
            166 -> type.anInt1125 = buffer.short.toInt()
            167 -> type.anInt1107 = buffer.readUnsignedShort()
            168 -> type.aBoolean1163 = true
            169 -> type.aBoolean1175 = true
            170 -> type.anInt1156 = buffer.readUnsignedSmart()
            171 -> type.anInt1111 = buffer.readUnsignedSmart()
            173 -> {
                type.anInt1128 = buffer.readUnsignedShort()
                type.anInt1159 = buffer.readUnsignedShort()
            }
            177 -> type.aBoolean1167 = true
            178 -> type.anInt1113 = buffer.readUnsignedByte()
            249 -> {
                val length = buffer.readUnsignedByte()
                (0 until length).forEach { _ ->
                    val string = buffer.readUnsignedByte().toBoolean()
                    type.params[buffer.readMedium()] = if (string) buffer.readString() else buffer.int
                }
            }
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }

    private fun skipReadModelIds(buffer: ByteBuffer) {
        val size: Int = buffer.readUnsignedByte()
        for (index in 0 until size) {
            buffer.position(buffer.position() + 1)
            val count: Int = buffer.readUnsignedByte()
            buffer.position(buffer.position() + (count * 2))
        }
    }
}