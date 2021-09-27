package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.extension.nameHash
import com.runetopic.cache.hierarchy.index.group.IGroup
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
): IIndex {
    override fun getId(): Int = id
    override fun getCRC(): Int = crc
    override fun getWhirlpool(): ByteArray = whirlpool
    override fun getCompression(): Int = compression
    override fun getProtocol(): Int = protocol
    override fun getRevision(): Int = revision
    override fun getIsNamed(): Boolean = isNamed

    override fun getGroups(): Map<Int, Js5Group> = groups
    override fun getGroup(groupId: Int): IGroup = groups[groupId] ?: Js5Group.DEFAULT
    override fun getGroup(groupName: String): IGroup = groups.values.find { it.getNameHash() == groupName.nameHash() } ?: Js5Group.DEFAULT
    override fun expand(): Int = groups.values.last().getFiles().size + (groups.values.last().getId() shl 8)
}