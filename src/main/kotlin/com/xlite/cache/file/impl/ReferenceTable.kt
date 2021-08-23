package com.xlite.cache.file.impl

import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class ReferenceTable(
    private val indexFile: IndexFile,
    private val id: Int,
    private val sector: Int,
    private val length: Int
) {
    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.indexFile)
        hash = 19 * hash + this.id
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other: ReferenceTable = obj as ReferenceTable
        if (indexFile != other.indexFile) {
            return false
        }
        if (id != other.id) {
            return false
        }
        if (sector != other.sector) {
            return false
        }
        if (this.length != other.length) {
            return false
        }
        return true
    }
}