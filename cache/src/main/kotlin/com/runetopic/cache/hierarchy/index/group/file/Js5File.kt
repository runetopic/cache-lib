package com.runetopic.cache.hierarchy.index.group.file

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class Js5File(
    private val groupId: Int = -1,
    private val id: Int = -1,
    private val nameHash: Int = -1,
    private var data: ByteArray? = null
): IFile {
    override fun getId(): Int = id
    override fun getGroupId(): Int = groupId
    override fun getNameHash(): Int = nameHash

    override fun getData(): ByteArray? = data
    override fun setData(data: ByteArray) {
        this.data = data
    }
}