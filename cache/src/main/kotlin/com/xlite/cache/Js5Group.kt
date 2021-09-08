package com.xlite.cache

import com.xlite.cache.exception.ArchiveNotFoundException
import com.xlite.cache.extension.nameHash

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class Js5Group(
    val id: Int,
    val crc: Int,
    val whirlpool: ByteArray,
    val compression: Int,
    val protocol: Int,
    val revision: Int,
    val isNamed: Boolean,
    val files: List<Js5File>
) {
    internal fun getFile(fileId: Int): Js5File = files[fileId]
    internal fun getFile(name: String): Js5File {
        return files.find { it.nameHash == name.nameHash() } ?: throw ArchiveNotFoundException("Could not find archive with name $name and name hash ${name.nameHash()}")
    }

    fun use(block: (Js5Group) -> Unit) {
        block.invoke(this)
    }

    fun capacity(): Int {
        return files.last().entries.size + (files.last().id * 256)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Js5Group

        if (id != other.id) return false
        if (crc != other.crc) return false
        if (!whirlpool.contentEquals(other.whirlpool)) return false
        if (compression != other.compression) return false
        if (protocol != other.protocol) return false
        if (revision != other.revision) return false
        if (isNamed != other.isNamed) return false
        if (files != other.files) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + crc
        result = 31 * result + whirlpool.contentHashCode()
        result = 31 * result + compression
        result = 31 * result + protocol
        result = 31 * result + revision
        result = 31 * result + isNamed.hashCode()
        result = 31 * result + files.hashCode()
        return result
    }
}