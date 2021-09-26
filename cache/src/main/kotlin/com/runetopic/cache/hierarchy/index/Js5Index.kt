package com.runetopic.cache.hierarchy.index

import com.runetopic.cache.extension.nameHash
import com.runetopic.cache.hierarchy.index.group.Js5Group
import com.runetopic.cache.hierarchy.index.group.file.Js5File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
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
    override fun getFiles(groupId: Int): Array<Js5File> = groups[groupId]?.getFiles()!!
    override fun expand(): Int = groups.values.last().getFiles().size + (groups.values.last().getId() shl 8)

    internal fun getGroup(groupId: Int): Js5Group? = groups[groupId]
    internal fun getGroup(groupName: String): Js5Group? = groups.values.find { it.getNameHash() == groupName.nameHash() }

    fun use(block: (Js5Index) -> Unit) = block.invoke(this)
}