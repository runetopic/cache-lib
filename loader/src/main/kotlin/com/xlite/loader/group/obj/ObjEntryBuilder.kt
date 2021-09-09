package com.xlite.cache.type.obj

import com.displee.cache.index.Index
import com.xlite.cache.exception.ReadEntryTypeException
import com.xlite.cache.type.IEntryBuilder
import com.xlite.ext.capacity
import com.xlite.ext.readString
import com.xlite.ext.toBoolean
import com.xlite.ext.writeString
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * @author Jordan Abraham
 */
internal class ObjEntryBuilder : IEntryBuilder<ObjEntryType> {

    lateinit var objs: Set<ObjEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(index: Index) {
        index.cache()

        objs = buildSet {
            (0 until index.capacity()).forEach {
                index.archive(it ushr 8)?.file(it and 0xFF)?.data?.let { data ->
                    add(read(Unpooled.wrappedBuffer(data), ObjEntryType(it)))
                }
            }
        }
        index.unCache()
    }

    override fun read(buf: ByteBuf, type: ObjEntryType): ObjEntryType {
        do when (val opcode: Int = buf.readUnsignedByte().toInt()) {
            0 -> break
            1 -> type.inventoryModel = buf.readUnsignedShort()
            2 -> type.name = buf.readString()
            4 -> type.zoom2d = buf.readUnsignedShort()
            5 -> type.xan2d = buf.readUnsignedShort()
            6 -> type.yan2d = buf.readUnsignedShort()
            7 -> {
                type.xOffset2d = buf.readUnsignedShort()
                if (type.xOffset2d > Short.MAX_VALUE) type.xOffset2d -= 65536
            }
            8 -> {
                type.yOffset2d = buf.readUnsignedShort()
                if (type.yOffset2d > Short.MAX_VALUE) type.yOffset2d -= 65536
            }
            11 -> type.stackable = 1
            12 -> type.cost = buf.readInt()
            16 -> type.members = true
            23 -> type.maleModel0 = buf.readUnsignedShort()
            24 -> type.maleModel1 = buf.readUnsignedShort()
            25 -> type.femaleModel0 = buf.readUnsignedShort()
            26 -> type.femaleModel1 = buf.readUnsignedShort()
            in 30..34 -> type.options[opcode - 30] = buf.readString()
            in 35..39 -> type.interfaceOptions[opcode - 35] = buf.readString()
            40 -> {
                val size = buf.readUnsignedByte().toInt()
                type.colorFind = ShortArray(size)
                type.colorReplace = ShortArray(size)
                (0 until size).forEach {
                    type.colorFind!![it] = buf.readUnsignedShort().toShort()
                    type.colorReplace!![it] = buf.readUnsignedShort().toShort()
                }
            }
            41 -> {
                val size = buf.readUnsignedByte().toInt()
                type.textureFind = ShortArray(size)
                type.textureReplace = ShortArray(size)
                (0 until size).forEach {
                    type.textureFind!![it] = buf.readUnsignedShort().toShort()
                    type.textureReplace!![it] = buf.readUnsignedShort().toShort()
                }
            }
            42 -> {
                val size = buf.readUnsignedByte().toInt()
                type.aByteArray1858 = ByteArray(size)
                (0 until size).forEach {
                    type.aByteArray1858!![it] = buf.readUnsignedByte().toByte()
                }
            }
            65 -> type.tradeable = true
            78 -> type.maleModel2 = buf.readUnsignedShort()
            79 -> type.femaleModel2 = buf.readUnsignedShort()
            90 -> type.maleHeadModel = buf.readUnsignedShort()
            91 -> type.femaleHeadModel = buf.readUnsignedShort()
            92 -> type.maleHeadModel2 = buf.readUnsignedShort()
            93 -> type.femaleHeadModel2 = buf.readUnsignedShort()
            95 -> type.zan2d = buf.readUnsignedShort()
            96 -> type.anInt1865 = buf.readUnsignedByte().toInt()
            97 -> type.notedId = buf.readUnsignedShort()
            98 -> type.notedTemplate = buf.readUnsignedShort()
            in 100..109 -> {
                if (type.countObj == null) {
                    type.countObj = IntArray(10)
                    type.countCo = IntArray(10)
                }
                type.countObj!![opcode - 100] = buf.readUnsignedShort()
                type.countCo!![opcode - 100] = buf.readUnsignedShort()
            }
            110 -> type.resizeX = buf.readUnsignedShort()
            111 -> type.resizeY = buf.readUnsignedShort()
            112 -> type.resizeZ = buf.readUnsignedShort()
            113 -> type.ambient = buf.readUnsignedByte().toInt()
            114 -> type.contrast = buf.readByte().toInt() * 5
            115 -> type.team = buf.readUnsignedByte().toInt()
            121 -> type.lendId = buf.readUnsignedShort()
            122 -> type.lendTemplateId = buf.readUnsignedShort()
            125 -> {
                type.anInt1895 = buf.readByte().toInt() shl 2
                type.anInt1862 = buf.readByte().toInt() shl 2
                type.anInt1873 = buf.readByte().toInt() shl 2
            }
            126 -> {
                type.anInt1866 = buf.readByte().toInt() shl 2
                type.anInt1852 = buf.readByte().toInt() shl 2
                type.anInt1867 = buf.readByte().toInt() shl 2
            }
            127 -> {
                type.anInt1899 = buf.readUnsignedByte().toInt()
                type.anInt1897 = buf.readUnsignedShort()
            }
            128 -> {
                type.anInt1850 = buf.readUnsignedByte().toInt()
                type.anInt1863 = buf.readUnsignedShort()
            }
            129 -> {
                type.anInt1896 = buf.readUnsignedByte().toInt()
                type.anInt1889 = buf.readUnsignedShort()
            }
            130 -> {
                type.anInt1842 = buf.readUnsignedByte().toInt()
                type.anInt1907 = buf.readUnsignedShort()
            }
            132 -> {
                val size = buf.readUnsignedByte().toInt()
                type.anIntArray1893 = IntArray(size)
                (0 until size).forEach {
                    type.anIntArray1893!![it] = buf.readUnsignedShort()
                }
            }
            134 -> type.anInt1902 = buf.readUnsignedByte().toInt()
            139 -> type.anInt1875 = buf.readUnsignedShort()
            140 -> type.anInt1885 = buf.readUnsignedShort()
            249 -> {
                val size = buf.readUnsignedByte().toInt()
                (0 until size).forEach { _ ->
                    val string = buf.readUnsignedByte().toInt().toBoolean()
                    type.params[buf.readMedium()] = if (string) buf.readString() else buf.readInt()
                }
            }
            else -> throw ReadEntryTypeException("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }

    override fun write(type: ObjEntryType): ByteBuf {
        val buf = Unpooled.buffer()

        if (type.inventoryModel != 0) {
            buf.writeByte(1)
            buf.writeShort(type.inventoryModel)
        }
        if (type.name != "null") {
            buf.writeByte(2)
            buf.writeString(type.name)
        }
        if (type.zoom2d != 2000) {
            buf.writeByte(4)
            buf.writeShort(type.zoom2d)
        }
        if (type.xan2d != 0) {
            buf.writeByte(5)
            buf.writeShort(type.xan2d)
        }
        if (type.yan2d != 0) {
            buf.writeByte(6)
            buf.writeShort(type.yan2d)
        }
        if (type.xOffset2d != 0) {
            buf.writeByte(7)
            buf.writeShort(type.xOffset2d + (if (type.xOffset2d < 0) 65536 else 0))
        }
        if (type.yOffset2d != 0) {
            buf.writeByte(8)
            buf.writeShort(type.yOffset2d + (if (type.yOffset2d < 0) 65536 else 0))
        }
        buf.writeByte(0)
        return buf
    }
}