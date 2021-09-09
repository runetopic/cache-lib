package com.xlite.cache.type.obj

import com.xlite.cache.type.IEntryType

/**
 * @author Jordan Abraham
 */
data class ObjEntryType(private val id: Int = 0) : IEntryType {
    var name = "null"
        internal set
    var resizeX = 128
        internal set
    var resizeY = 128
        internal set
    var resizeZ = 128
        internal set
    var xan2d = 0
        internal set
    var yan2d = 0
        internal set
    var zan2d = 0
        internal set
    var anInt1865 = 0
        internal set
    var cost = 1
        internal set
    var tradeable = false
        internal set
    var stackable = 0
        internal set
    var inventoryModel = 0
        internal set
    var members = false
        internal set
    var colorFind: ShortArray? = null
        internal set
    var colorReplace: ShortArray? = null
        internal set
    var textureFind: ShortArray? = null
        internal set
    var textureReplace: ShortArray? = null
        internal set
    var aByteArray1858: ByteArray? = null
        internal set
    var zoom2d = 2000
        internal set
    var xOffset2d = 0
        internal set
    var yOffset2d = 0
        internal set
    var ambient = 0
        internal set
    var contrast = 0
        internal set
    var countCo: IntArray? = null
        internal set
    var countObj: IntArray? = null
        internal set
    var options = arrayOf(
        null, null, "Take", null, null
    )
        internal set
    var interfaceOptions = arrayOf(
        null, null, null, null, "Drop"
    )
        internal set
    var maleModel0 = -1
        internal set
    var maleModel1 = -1
        internal set
    var maleModel2 = -1
        internal set
    var maleHeadModel = -1
        internal set
    var maleHeadModel2 = -1
        internal set
    var femaleModel0 = -1
        internal set
    var femaleModel1 = -1
        internal set
    var femaleModel2 = -1
        internal set
    var femaleHeadModel = -1
        internal set
    var femaleHeadModel2 = -1
        internal set
    var notedId = -1
        internal set
    var notedTemplate = -1
        internal set
    var team = 0
        internal set
    var lendId = -1
        internal set
    var lendTemplateId = -1
        internal set
    var anInt1895 = 0
        internal set
    var anInt1862 = 0
        internal set
    var anInt1873 = 0
        internal set
    var anInt1866 = 0
        internal set
    var anInt1852 = 0
        internal set
    var anInt1867 = 0
        internal set
    var anInt1899 = -1
        internal set
    var anInt1897 = -1
        internal set
    var anInt1850 = -1
        internal set
    var anInt1863 = -1
        internal set
    var anInt1896 = -1
        internal set
    var anInt1889 = -1
        internal set
    var anInt1842 = -1
        internal set
    var anInt1907 = -1
        internal set
    var anIntArray1893: IntArray? = null
        internal set
    var anInt1902 = 0
        internal set
    var anInt1875 = -1
        internal set
    var anInt1885 = -1
        internal set
    var params = mutableMapOf<Int, Any>()
        internal set

    override fun getId(): Int {
        return id
    }
}