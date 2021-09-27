package com.runetopic.cache.hierarchy.index.group

import com.runetopic.cache.hierarchy.index.group.file.IFile
import com.runetopic.cache.hierarchy.index.group.file.Js5File

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
class Js5Group(
    private val id: Int,
    private val nameHash: Int,
    private val crc: Int,
    private val whirlpool: ByteArray,
    private val revision: Int,
    private val keys: IntArray,
    private val files: Map<Int, IFile>,
    private val data: ByteArray
): IGroup {
    override fun getId(): Int = id
    override fun getNameHash(): Int = nameHash
    override fun getCRC(): Int = crc
    override fun getWhirlpool(): ByteArray = whirlpool
    override fun getRevision(): Int = revision
    override fun getKeys(): IntArray = keys
    override fun getFiles(): Map<Int, IFile> = files
    override fun getData(): ByteArray = data

    override fun getFile(fileId: Int): IFile = files[fileId] ?: Js5File.DEFAULT

    internal companion object {
        val DEFAULT = Js5Group(-1, -1, -1, byteArrayOf(), -1, intArrayOf(), mapOf(), byteArrayOf())
    }
}
