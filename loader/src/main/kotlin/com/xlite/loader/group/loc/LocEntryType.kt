package com.xlite.loader.group.loc

import com.xlite.loader.IEntryType

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class LocEntryType(
    private val id: Int= 0,
    var types: ByteArray? = null,
    var models: Array<IntArray?>? = null,
    var name: String = "null",
    var sizeX: Int = -1,
    var sizeZ: Int = -1,
    var interactionType: Int = 2,
    var shouldBlockProjectiles: Boolean = true,
    var wallOrDoor: Int = -1,
    var contouredGround: Byte = 0,
    var shouldMergeNormals: Boolean = false,
    var anInt1146: Int = -1,
    var animationId: Int = -1,
    var decorDisplacement: Int = 64,
    var ambient: Int = 0,
    var contrast: Int = 0,
    var actions: Array<String> = Array(5) { "" },
    var colorsToFind: ShortArray? = null,
    var colorsToReplace: ShortArray? = null,
    var texturesToFind: ShortArray? = null,
    var texturesToReplace: ShortArray? = null,
    var aByteArray1118: ByteArray? = null,
    var isRotated: Boolean = false,
    var shadowed: Boolean = false,
    var modelSizeX: Int = 128,
    var modelSizeHeight: Int = 128,
    var modelSizeZ: Int = 128,
    var clippingFlag: Int = 0,
    var anInt1150: Int = 0,
    var anInt1149: Int = 0,
    var anInt1124: Int = 0,
    var aBoolean1131: Boolean = false,
    var aBoolean1157: Boolean = false,
    var anInt1166: Int = -1,
    var varbitId: Int = -1,
    var varpId: Int = -1,
    var anInt1132: Int = -1,
    var anInt1144: Int = 0,
    var anInt1145: Int = 0,
    var anInt1139: Int = 0,
    var aBoolean1108: Boolean = false,
    var aBoolean1151: Boolean = false,
    var aBoolean1103: Boolean = false,
    var aBoolean1171: Boolean = false,
    var aBoolean1104: Boolean = false,
    var aBoolean1106: Boolean = false,
    var configChangeDest: IntArray? = null,
    var anIntArray1127: IntArray? = null,
    var anInt1142: Int = -1,
    var anInt1184: Int = -1,
    var anInt1173: Int = -1,
    var anInt1183: Int = -1,
    var anInt1121: Int = -1,
    var anInt1181: Int = 0,
    var anInt1160: Int = 0,
    var anInt1136: Int = 255,
    var anInt1116: Int = 0,
    var anInt1101: Int = -1,
    var aBoolean1148: Boolean = false,
    var anIntArray1170: IntArray? = null,
    var anIntArray1154: IntArray? = null,
    var anIntArray1153: IntArray? = null,
    var aByte1123: Byte = 0,
    var aByte1110: Byte = 0,
    var aByte1169: Byte = 0,
    var aByte1109: Byte = 0,
    var anInt1112: Int = 0,
    var anInt2650: Int = 0,
    var anInt1115: Int = 0,
    var anInt1125: Int = 0,
    var anInt1107: Int = 0,
    var aBoolean1163: Boolean = false,
    var aBoolean1175: Boolean = false,
    var anInt1156: Int = 960,
    var anInt1111: Int = 0,
    var anInt1128: Int = 256,
    var anInt1159: Int = 256,
    var aBoolean1167: Boolean = false,
    var anInt1113: Int = 0,
    var params: MutableMap<Int, Any> = mutableMapOf(),
    ): IEntryType {
    override fun getId(): Int = id
}