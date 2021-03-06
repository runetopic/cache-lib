package com.runetopic.loader.index.npc

import com.runetopic.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class NpcEntryType(
    private val id: Int = 0,
    var aBoolean812: Boolean = true,
    var combatLevel: Int = -1,
    var hasRenderPriority: Boolean = false,
    var anInt805: Int = 0,
    var anInt803: Int = -1,
    var aByte806: Byte = -96,
    var anInt835: Int = -1,
    var aShort834: Short = 0,
    var anInt847: Int = 0,
    var anInt811: Int = -1,
    var anInt846: Int = -1,
    var isMinimapVisible: Boolean = true,
    var anInt830: Int = -1,
    var anInt831: Int = -1,
    var name: String = "null",
    var isInteractable: Boolean = true,
    var heightScale: Int = 128,
    var varbitId: Int = -1,
    var aByte833: Byte = -1,
    var aByte855: Byte = 0,
    var anInt823: Int = -1,
    var widthScale: Int = 128,
    var anInt826: Int = -1,
    var anInt850: Int = -1,
    var aByte858: Byte = 0,
    var anInt856: Int = -1,
    var anInt857: Int = -1,
    var anInt861: Int = -1,
    var aByte819: Byte = 4,
    var size: Int = 1,
    var ambient: Int = 0,
    var varpId: Int = -1,
    var anInt837: Int = 256,
    var anInt864: Int = -1,
    var aBoolean869: Boolean = false,
    var anInt828: Int = 0,
    var anInt848: Int = 256,
    var anInt852: Int = -1,
    var aShort865: Short = 0,
    var options: Array<String?> = arrayOfNulls(5),
    var headIcon: Int = -1,
    var rotationFlag: Boolean = true,
    var anInt854: Int = -1,
    var aBoolean875: Boolean = false,
    var rotationSpeed: Int = 32,
    var anInt808: Int = 255,
    var contrast: Int = 0,
    var models: IntArray? = null,
    var colorToFind: ShortArray? = null,
    var colorToReplace: ShortArray? = null,
    var textureToFind: ShortArray? = null,
    var textureToReplace: ShortArray? = null,
    var aByteArray866: ByteArray? = null,
    var chatheadModels: IntArray? = null,
    var configChangeDest: IntArray? = null,
    var aByte804: Byte = -16,
    var anIntArrayArray845: Array<IntArray?>? = null,
    var anInt817: Int = -1,
    var aByte821: Byte = 0,
    var aByte824: Byte = 0,
    var aByte843: Byte = 0,
    var aBoolean809: Boolean = false,
    var params: MutableMap<Int, Any> = mutableMapOf(),
): IEntryType {
    override fun getId(): Int = id
}