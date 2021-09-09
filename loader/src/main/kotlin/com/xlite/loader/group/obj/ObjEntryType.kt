package com.xlite.loader.group.obj

import com.xlite.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class ObjEntryType(
    private val id: Int = 0,
    var name: String = "null",
    var resizeX: Int = 128,
    var resizeY: Int = 128,
    var resizeZ: Int = 128,
    var xan2d: Int = 0,
    var yan2d: Int = 0,
    var zan2d: Int = 0,
    var anInt1865: Int = 0,
    var cost: Int = 1,
    var tradeable: Boolean = false,
    var stackable: Int = 0,
    var inventoryModel: Int = 0,
    var members: Boolean = false,
    var colorFind: ShortArray? = null,
    var colorReplace: ShortArray? = null,
    var textureFind: ShortArray? = null,
    var textureReplace: ShortArray? = null,
    var aByteArray1858: ByteArray? = null,
    var zoom2d: Int = 2000,
    var xOffset2d: Int = 0,
    var yOffset2d: Int = 0,
    var ambient: Int = 0,
    var contrast: Int = 0,
    var countCo: IntArray? = null,
    var countObj: IntArray? = null,
    var options: Array<String?> = arrayOf(null, null, "Take", null, null),
    var interfaceOptions: Array<String?> = arrayOf(null, null, null, null, "Drop"),
    var maleModel0: Int = -1,
    var maleModel1: Int = -1,
    var maleModel2: Int = -1,
    var maleHeadModel: Int = -1,
    var maleHeadModel2: Int = -1,
    var femaleModel0: Int = -1,
    var femaleModel1: Int = -1,
    var femaleModel2: Int = -1,
    var femaleHeadModel: Int = -1,
    var femaleHeadModel2: Int = -1,
    var notedId: Int = -1,
    var notedTemplate: Int = -1,
    var team: Int = 0,
    var lendId: Int = -1,
    var lendTemplateId: Int = -1,
    var anInt1895: Int = 0,
    var anInt1862: Int = 0,
    var anInt1873: Int = 0,
    var anInt1866: Int = 0,
    var anInt1852: Int = 0,
    var anInt1867: Int = 0,
    var anInt1899: Int = -1,
    var anInt1897: Int = -1,
    var anInt1850: Int = -1,
    var anInt1863: Int = -1,
    var anInt1896: Int = -1,
    var anInt1889: Int = -1,
    var anInt1842: Int = -1,
    var anInt1907: Int = -1,
    var anIntArray1893: IntArray? = null,
    var anInt1902: Int = 0,
    var anInt1875: Int = -1,
    var anInt1885: Int = -1,
    var params: MutableMap<Int, Any> = mutableMapOf(),
) : IEntryType {

    override fun getId(): Int = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjEntryType

        if (id != other.id) return false
        if (name != other.name) return false
        if (resizeX != other.resizeX) return false
        if (resizeY != other.resizeY) return false
        if (resizeZ != other.resizeZ) return false
        if (xan2d != other.xan2d) return false
        if (yan2d != other.yan2d) return false
        if (zan2d != other.zan2d) return false
        if (anInt1865 != other.anInt1865) return false
        if (cost != other.cost) return false
        if (tradeable != other.tradeable) return false
        if (stackable != other.stackable) return false
        if (inventoryModel != other.inventoryModel) return false
        if (members != other.members) return false
        if (colorFind != null) {
            if (other.colorFind == null) return false
            if (!colorFind.contentEquals(other.colorFind)) return false
        } else if (other.colorFind != null) return false
        if (colorReplace != null) {
            if (other.colorReplace == null) return false
            if (!colorReplace.contentEquals(other.colorReplace)) return false
        } else if (other.colorReplace != null) return false
        if (textureFind != null) {
            if (other.textureFind == null) return false
            if (!textureFind.contentEquals(other.textureFind)) return false
        } else if (other.textureFind != null) return false
        if (textureReplace != null) {
            if (other.textureReplace == null) return false
            if (!textureReplace.contentEquals(other.textureReplace)) return false
        } else if (other.textureReplace != null) return false
        if (aByteArray1858 != null) {
            if (other.aByteArray1858 == null) return false
            if (!aByteArray1858.contentEquals(other.aByteArray1858)) return false
        } else if (other.aByteArray1858 != null) return false
        if (zoom2d != other.zoom2d) return false
        if (xOffset2d != other.xOffset2d) return false
        if (yOffset2d != other.yOffset2d) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (countCo != null) {
            if (other.countCo == null) return false
            if (!countCo.contentEquals(other.countCo)) return false
        } else if (other.countCo != null) return false
        if (countObj != null) {
            if (other.countObj == null) return false
            if (!countObj.contentEquals(other.countObj)) return false
        } else if (other.countObj != null) return false
        if (!options.contentEquals(other.options)) return false
        if (!interfaceOptions.contentEquals(other.interfaceOptions)) return false
        if (maleModel0 != other.maleModel0) return false
        if (maleModel1 != other.maleModel1) return false
        if (maleModel2 != other.maleModel2) return false
        if (maleHeadModel != other.maleHeadModel) return false
        if (maleHeadModel2 != other.maleHeadModel2) return false
        if (femaleModel0 != other.femaleModel0) return false
        if (femaleModel1 != other.femaleModel1) return false
        if (femaleModel2 != other.femaleModel2) return false
        if (femaleHeadModel != other.femaleHeadModel) return false
        if (femaleHeadModel2 != other.femaleHeadModel2) return false
        if (notedId != other.notedId) return false
        if (notedTemplate != other.notedTemplate) return false
        if (team != other.team) return false
        if (lendId != other.lendId) return false
        if (lendTemplateId != other.lendTemplateId) return false
        if (anInt1895 != other.anInt1895) return false
        if (anInt1862 != other.anInt1862) return false
        if (anInt1873 != other.anInt1873) return false
        if (anInt1866 != other.anInt1866) return false
        if (anInt1852 != other.anInt1852) return false
        if (anInt1867 != other.anInt1867) return false
        if (anInt1899 != other.anInt1899) return false
        if (anInt1897 != other.anInt1897) return false
        if (anInt1850 != other.anInt1850) return false
        if (anInt1863 != other.anInt1863) return false
        if (anInt1896 != other.anInt1896) return false
        if (anInt1889 != other.anInt1889) return false
        if (anInt1842 != other.anInt1842) return false
        if (anInt1907 != other.anInt1907) return false
        if (anIntArray1893 != null) {
            if (other.anIntArray1893 == null) return false
            if (!anIntArray1893.contentEquals(other.anIntArray1893)) return false
        } else if (other.anIntArray1893 != null) return false
        if (anInt1902 != other.anInt1902) return false
        if (anInt1875 != other.anInt1875) return false
        if (anInt1885 != other.anInt1885) return false
        if (params != other.params) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + resizeX
        result = 31 * result + resizeY
        result = 31 * result + resizeZ
        result = 31 * result + xan2d
        result = 31 * result + yan2d
        result = 31 * result + zan2d
        result = 31 * result + anInt1865
        result = 31 * result + cost
        result = 31 * result + tradeable.hashCode()
        result = 31 * result + stackable
        result = 31 * result + inventoryModel
        result = 31 * result + members.hashCode()
        result = 31 * result + (colorFind?.contentHashCode() ?: 0)
        result = 31 * result + (colorReplace?.contentHashCode() ?: 0)
        result = 31 * result + (textureFind?.contentHashCode() ?: 0)
        result = 31 * result + (textureReplace?.contentHashCode() ?: 0)
        result = 31 * result + (aByteArray1858?.contentHashCode() ?: 0)
        result = 31 * result + zoom2d
        result = 31 * result + xOffset2d
        result = 31 * result + yOffset2d
        result = 31 * result + ambient
        result = 31 * result + contrast
        result = 31 * result + (countCo?.contentHashCode() ?: 0)
        result = 31 * result + (countObj?.contentHashCode() ?: 0)
        result = 31 * result + options.contentHashCode()
        result = 31 * result + interfaceOptions.contentHashCode()
        result = 31 * result + maleModel0
        result = 31 * result + maleModel1
        result = 31 * result + maleModel2
        result = 31 * result + maleHeadModel
        result = 31 * result + maleHeadModel2
        result = 31 * result + femaleModel0
        result = 31 * result + femaleModel1
        result = 31 * result + femaleModel2
        result = 31 * result + femaleHeadModel
        result = 31 * result + femaleHeadModel2
        result = 31 * result + notedId
        result = 31 * result + notedTemplate
        result = 31 * result + team
        result = 31 * result + lendId
        result = 31 * result + lendTemplateId
        result = 31 * result + anInt1895
        result = 31 * result + anInt1862
        result = 31 * result + anInt1873
        result = 31 * result + anInt1866
        result = 31 * result + anInt1852
        result = 31 * result + anInt1867
        result = 31 * result + anInt1899
        result = 31 * result + anInt1897
        result = 31 * result + anInt1850
        result = 31 * result + anInt1863
        result = 31 * result + anInt1896
        result = 31 * result + anInt1889
        result = 31 * result + anInt1842
        result = 31 * result + anInt1907
        result = 31 * result + (anIntArray1893?.contentHashCode() ?: 0)
        result = 31 * result + anInt1902
        result = 31 * result + anInt1875
        result = 31 * result + anInt1885
        result = 31 * result + params.hashCode()
        return result
    }
}