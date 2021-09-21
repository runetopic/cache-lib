package com.runetopic.loader.index.obj

import com.runetopic.cache.extension.*
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal class ObjEntryBuilder : IEntryBuilder<ObjEntryType> {

    lateinit var objs: Set<ObjEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        objs = buildSet {
            store.index(19).use { index ->
                (0..index.expand()).forEach {
                    add(read(ByteBuffer.wrap(store.file(index, it ushr 8, it and 0xFF).data), ObjEntryType(it)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: ObjEntryType): ObjEntryType {
        do when (val opcode: Int = buffer.readUnsignedByte()) {
            0 -> break
            1 -> type.inventoryModel = buffer.readUnsignedShort()
            2 -> type.name = buffer.readString()
            4 -> type.zoom2d = buffer.readUnsignedShort()
            5 -> type.xan2d = buffer.readUnsignedShort()
            6 -> type.yan2d = buffer.readUnsignedShort()
            7 -> buffer.readUnsignedShort().let {
                type.xOffset2d = if (it > Short.MAX_VALUE) it - 65536 else it
            }
            8 -> buffer.readUnsignedShort().let {
                type.yOffset2d = if (it > Short.MAX_VALUE) it - 65536 else it
            }
            11 -> type.stackable = 1
            12 -> type.cost = buffer.int
            16 -> type.members = true
            23 -> type.maleModel0 = buffer.readUnsignedShort()
            24 -> type.maleModel1 = buffer.readUnsignedShort()
            25 -> type.femaleModel0 = buffer.readUnsignedShort()
            26 -> type.femaleModel1 = buffer.readUnsignedShort()
            in 30..34 -> buffer.readString().let { type.options[opcode - 30] = it }
            in 35..39 -> buffer.readString().let { type.interfaceOptions[opcode - 35] = it }
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
                val aByteArray1858 = ByteArray(size)
                (0 until size).forEach { aByteArray1858[it] = buffer.readUnsignedByte().toByte() }
                type.aByteArray1858 = aByteArray1858
            }
            65 -> type.tradeable = true
            78 -> type.maleModel2 = buffer.readUnsignedShort()
            79 -> type.femaleModel2 = buffer.readUnsignedShort()
            90 -> type.maleHeadModel = buffer.readUnsignedShort()
            91 -> type.femaleHeadModel = buffer.readUnsignedShort()
            92 -> type.maleHeadModel2 = buffer.readUnsignedShort()
            93 -> type.femaleHeadModel2 = buffer.readUnsignedShort()
            95 -> type.zan2d = buffer.readUnsignedShort()
            96 -> type.anInt1865 = buffer.readUnsignedByte()
            97 -> type.notedId = buffer.readUnsignedShort()
            98 -> type.notedTemplate = buffer.readUnsignedShort()
            in 100..109 -> {
                val countObj = IntArray(10)
                val countCo = IntArray(10)
                countObj[opcode - 100] = buffer.readUnsignedShort()
                countCo[opcode - 100] = buffer.readUnsignedShort()
                type.countObj = countObj
                type.countCo = countCo
            }
            110 -> type.resizeX = buffer.readUnsignedShort()
            111 -> type.resizeY = buffer.readUnsignedShort()
            112 -> type.resizeZ = buffer.readUnsignedShort()
            113 -> type.ambient = buffer.readUnsignedByte()
            114 -> type.contrast = buffer.get().toInt() * 5
            115 -> type.team = buffer.readUnsignedByte()
            121 -> type.lendId = buffer.readUnsignedShort()
            122 -> type.lendTemplateId = buffer.readUnsignedShort()
            125 -> {
                type.anInt1895 = buffer.get().toInt() shl 2
                type.anInt1862 = buffer.get().toInt() shl 2
                type.anInt1873 = buffer.get().toInt() shl 2
            }
            126 -> {
                type.anInt1866 = buffer.get().toInt() shl 2
                type.anInt1852 = buffer.get().toInt() shl 2
                type.anInt1867 = buffer.get().toInt() shl 2
            }
            127 -> {
                type.anInt1899 = buffer.readUnsignedByte()
                type.anInt1897 = buffer.readUnsignedShort()
            }
            128 -> {
                type.anInt1850 = buffer.readUnsignedByte()
                type.anInt1863 = buffer.readUnsignedShort()
            }
            129 -> {
                type.anInt1896 = buffer.readUnsignedByte()
                type.anInt1889 = buffer.readUnsignedShort()
            }
            130 -> {
                type.anInt1842 = buffer.readUnsignedByte()
                type.anInt1907 = buffer.readUnsignedShort()
            }
            132 -> {
                val size = buffer.readUnsignedByte()
                val anIntArray1893 = IntArray(size)
                (0 until size).forEach { anIntArray1893[it] = buffer.readUnsignedShort() }
                type.anIntArray1893 = anIntArray1893
            }
            134 -> type.anInt1902 = buffer.readUnsignedByte()
            139 -> type.anInt1875 = buffer.readUnsignedShort()
            140 -> type.anInt1885 = buffer.readUnsignedShort()
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