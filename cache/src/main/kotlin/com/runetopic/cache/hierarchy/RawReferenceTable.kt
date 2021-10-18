package com.runetopic.cache.hierarchy

/**
 * @author Jordan Abraham
 */
data class RawReferenceTable(
    val count: Int,
    val isNamed: Boolean,
    val groupIds: IntArray,
    val groupTables: List<ByteArray>,
    val groupNameHashes: IntArray,
    val groupCrcs: IntArray,
    val groupWhirlpools: Array<ByteArray>,
    val groupRevisions: IntArray,
    val groupFileIds: IntArray,
    val fileIds: Array<IntArray>,
    val fileNameHashes: Array<IntArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawReferenceTable

        if (!groupIds.contentEquals(other.groupIds)) return false
        if (groupTables != other.groupTables) return false
        if (!groupNameHashes.contentEquals(other.groupNameHashes)) return false
        if (!groupCrcs.contentEquals(other.groupCrcs)) return false
        if (!groupWhirlpools.contentDeepEquals(other.groupWhirlpools)) return false
        if (!groupRevisions.contentEquals(other.groupRevisions)) return false
        if (!groupFileIds.contentEquals(other.groupFileIds)) return false
        if (!fileIds.contentDeepEquals(other.fileIds)) return false
        if (!fileNameHashes.contentDeepEquals(other.fileNameHashes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupIds.contentHashCode()
        result = 31 * result + groupTables.hashCode()
        result = 31 * result + groupNameHashes.contentHashCode()
        result = 31 * result + groupCrcs.contentHashCode()
        result = 31 * result + groupWhirlpools.contentDeepHashCode()
        result = 31 * result + groupRevisions.contentHashCode()
        result = 31 * result + groupFileIds.contentHashCode()
        result = 31 * result + fileIds.contentDeepHashCode()
        result = 31 * result + fileNameHashes.contentDeepHashCode()
        return result
    }
}