package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
class Js5File(
    private val groupId: Int,
    private val id: Int,
    private val nameHash: Int,
    private val data: ByteArray
): IFile {
    override fun getId(): Int = id
    override fun getGroupId(): Int = groupId
    override fun getNameHash(): Int = nameHash
    override fun getData(): ByteArray = data

    internal companion object {
        val DEFAULT = Js5File(-1, -1, -1, byteArrayOf(0))
    }
}