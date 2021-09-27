package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.extension.nameHash
import com.runetopic.cache.hierarchy.index.group.Group
import com.runetopic.cache.hierarchy.index.group.Js5Group

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
class Js5Index(
    private val id: Int,
    private val crc: Int,
    private val whirlpool: ByteArray,
    private val compression: Int,
    private val protocol: Int,
    private val revision: Int,
    private val isNamed: Boolean,
    private val groups: Map<Int, Js5Group>
): Index {
    override fun getId(): Int = id
    override fun getCRC(): Int = crc
    override fun getWhirlpool(): ByteArray = whirlpool
    override fun getCompression(): Int = compression
    override fun getProtocol(): Int = protocol
    override fun getRevision(): Int = revision
    override fun getIsNamed(): Boolean = isNamed

    override fun getGroups(): Collection<Group> = groups.values
    override fun getGroup(groupId: Int): Group = groups[groupId] ?: Js5Group.DEFAULT
    override fun getGroup(groupName: String): Group = groups.values.find { it.getNameHash() == groupName.nameHash() } ?: Js5Group.DEFAULT
    override fun expand(): Int = groups.values.last().getFiles().size + (groups.values.last().getId() shl 8)

    internal companion object {
        fun default(indexId: Int): Js5Index = Js5Index(indexId, 0, ByteArray(64), -1, -1, 0, false, hashMapOf())
    }
}