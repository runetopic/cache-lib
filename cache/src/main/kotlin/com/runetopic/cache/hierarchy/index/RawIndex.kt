package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.hierarchy.RawReferenceTable
import com.runetopic.cache.hierarchy.index.group.Group

/**
 * @author Jordan Abraham
 */
class RawIndex(
    private val id: Int,
    private val crc: Int,
    private val whirlpool: ByteArray,
    private val compression: Int,
    private val protocol: Int,
    private val revision: Int,
    val referenceTable: RawReferenceTable
): Index {
    override fun getId(): Int = id
    override fun getCRC(): Int = crc
    override fun getWhirlpool(): ByteArray = whirlpool
    override fun getCompression(): Int = compression
    override fun getProtocol(): Int = protocol
    override fun getRevision(): Int = revision
    override fun getIsNamed(): Boolean = referenceTable.isNamed

    override fun getGroups(): Collection<Group> = throw RuntimeException()
    override fun getGroup(groupId: Int): Group = throw RuntimeException()
    override fun getGroup(groupName: String): Group = throw RuntimeException()
    override fun expand(): Int = throw RuntimeException()
}