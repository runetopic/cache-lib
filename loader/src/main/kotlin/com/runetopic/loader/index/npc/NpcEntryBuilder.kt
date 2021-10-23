package com.runetopic.loader.index.npc

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryBuilder
import com.runetopic.loader.extension.*
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
class NpcEntryBuilder: IEntryBuilder<NpcEntryType> {

    lateinit var npcs: Set<NpcEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Js5Store) {
        npcs = buildSet {
            store.index(18).use { index ->
                (0 until index.expand()).forEach {
                    add(read(index.group(it ushr 8).file(it and 0xFF).data.toByteBuffer(), NpcEntryType(it)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: NpcEntryType): NpcEntryType {
        do when (val opcode: Int = buffer.readUnsignedByte()) {
            0 -> break
            1 -> {
                val size = buffer.readUnsignedByte()
                val models = IntArray(size)
                (0 until size).forEach {
                    models[it] = buffer.readUnsignedShort()
                    if (models[it] == 65535) {
                        models[it] = -1
                    }
                }
                type.models = models
            }
            2 -> type.name = buffer.readString()
            12 -> type.size = buffer.readUnsignedByte()
            in 30..34 -> type.options[opcode - 30] = buffer.readString()
            40 -> {
                val size = buffer.readUnsignedByte()
                val colorToFind = ShortArray(size)
                val colorToReplace = ShortArray(size)
                (0 until size).forEach {
                    colorToFind[it] = buffer.readUnsignedShort().toShort()
                    colorToReplace[it] = buffer.readUnsignedShort().toShort()
                }
                type.colorToFind = colorToFind
                type.colorToReplace = colorToReplace
            }
            41 -> {
                val size = buffer.readUnsignedByte()
                val textureToFind = ShortArray(size)
                val textureToReplace = ShortArray(size)
                (0 until size).forEach {
                    textureToFind[it] = buffer.readUnsignedShort().toShort()
                    textureToReplace[it] = buffer.readUnsignedShort().toShort()
                }
                type.textureToFind = textureToFind
                type.textureToReplace = textureToReplace
            }
            42 -> {
                val size = buffer.readUnsignedByte()
                val aByteArray866 = ByteArray(size)
                (0 until size).forEach {
                    aByteArray866[it] = buffer.get()
                }
                type.aByteArray866 = aByteArray866
            }
            60 -> {
                val size = buffer.readUnsignedByte()
                val chatheadModels = IntArray(size)
                (0 until size).forEach {
                    chatheadModels[it] = buffer.readUnsignedShort()
                }
                type.chatheadModels = chatheadModels
            }
            93 -> type.isMinimapVisible = false
            95 -> type.combatLevel = buffer.readUnsignedShort()
            97 -> type.widthScale = buffer.readUnsignedShort()
            98 -> type.heightScale = buffer.readUnsignedShort()
            99 -> type.hasRenderPriority = true
            100 -> type.ambient = buffer.get().toInt()
            101 -> type.contrast = buffer.get() * 5
            102 -> type.headIcon = buffer.readUnsignedShort()
            103 -> type.rotationSpeed = buffer.readUnsignedShort()
            106, 118 -> {
                type.varbitId = buffer.readUnsignedShort()
                if (type.varbitId == 65535) {
                    type.varbitId = -1
                }
                type.varpId = buffer.readUnsignedShort()
                if (type.varpId == 65535) {
                    type.varpId = -1
                }
                var value = -1
                if (opcode == 118) {
                    value = buffer.readUnsignedShort()
                    if (value == 65535) {
                        value = -1
                    }
                }
                val size = buffer.readUnsignedByte()
                val configChangeDest = IntArray(size + 2)
                (0..size).forEach {
                    configChangeDest[it] = buffer.readUnsignedShort()
                    if (configChangeDest[it] == 65535) {
                        configChangeDest[it] = -1
                    }
                }
                configChangeDest[size + 1] = value
                type.configChangeDest = configChangeDest
            }
            107 -> type.isInteractable = false
            109 -> type.rotationFlag = false
            111 -> type.aBoolean812 = false
            113 -> {
                type.aShort865 = buffer.readUnsignedShort().toShort()
                type.aShort834 = buffer.readUnsignedShort().toShort()
            }
            114 -> {
                type.aByte806 = buffer.get()
                type.aByte804 = buffer.get()
            }
            119 -> type.aByte858 = buffer.get()
            121 -> {
                val anIntArrayArray845 = arrayOfNulls<IntArray>(type.models!!.size)
                val size = buffer.readUnsignedByte()
                (0 until size).forEach {
                    val i_98_ = buffer.readUnsignedByte()
                    val data = (IntArray(3).also { anIntArrayArray845[i_98_] = it })
                    data[0] = buffer.get().toInt()
                    data[1] = buffer.get().toInt()
                    data[2] = buffer.get().toInt()
                }
            }
            122 -> type.anInt854 = buffer.readUnsignedShort()
            123 -> type.anInt852 = buffer.readUnsignedShort()
            125 -> type.aByte819 = buffer.get()
            127 -> type.anInt835 = buffer.readUnsignedShort()
            128 -> buffer.skip(1)
            134 -> {
                type.anInt826 = buffer.readUnsignedShort()
                if (type.anInt826 == 65535) {
                    type.anInt826 = -1
                }
                type.anInt850 = buffer.readUnsignedShort()
                if (type.anInt850 == 65535) {
                    type.anInt850 = -1
                }
                type.anInt811 = buffer.readUnsignedShort()
                if (type.anInt811 == 65535) {
                    type.anInt811 = -1
                }
                type.anInt856 = buffer.readUnsignedShort()
                if (type.anInt856 == 65535) {
                    type.anInt856 = -1
                }
                type.anInt805 = buffer.readUnsignedByte()
            }
            135 -> {
                type.anInt803 = buffer.readUnsignedByte()
                type.anInt861 = buffer.readUnsignedShort()
            }
            136 -> {
                type.anInt823 = buffer.readUnsignedByte()
                type.anInt857 = buffer.readUnsignedShort()
            }
            137 -> type.anInt831 = buffer.readUnsignedShort()
            138 -> type.anInt817 = buffer.readUnsignedShort()
            139 -> type.anInt830 = buffer.readUnsignedShort()
            140 -> type.anInt808 = buffer.readUnsignedByte()
            141 -> type.aBoolean875 = true
            142 -> type.anInt846 = buffer.readUnsignedShort()
            143 -> type.aBoolean869 = true
            in 150..154 -> { buffer.readString().let { type.options[opcode -150] = it } }
            155 -> {
                type.aByte821 = buffer.get()
                type.aByte824 = buffer.get()
                type.aByte843 = buffer.get()
                type.aByte855 = buffer.get()
            }
            158 -> type.aByte833 = 1
            159 -> type.aByte833 = 0
            160 -> {
                val size = buffer.readUnsignedByte()
                val anIntArray867 = IntArray(size)
                (0 until size).forEach {
                    anIntArray867[it] = buffer.readUnsignedShort()
                }
            }
            162 -> type.aBoolean809 = true
            163 -> type.anInt864 = buffer.readUnsignedByte()
            164 -> {
                type.anInt848 = buffer.readUnsignedShort()
                type.anInt837 = buffer.readUnsignedShort()
            }
            165 -> type.anInt847 = buffer.readUnsignedByte()
            168 -> type.anInt828 = buffer.readUnsignedByte()
            249 -> {
                val length = buffer.readUnsignedByte()
                (0 until length).forEach { _ ->
                    val string = buffer.readUnsignedByte().toBoolean()
                    type.params[buffer.readUnsignedMedium()] = if (string) buffer.readString() else buffer.int
                }
            }
        } while (true)
        return type
    }
}