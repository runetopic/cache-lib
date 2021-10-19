package com.runetopic.cache.hierarchy

import com.runetopic.cache.store.storage.js5.IIdxFile
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal data class ReferenceTable(
    val idxFile: IIdxFile,
    val id: Int,
    val sector: Int,
    val length: Int
) {
    fun exists(): Boolean = (length != 0 && sector != 0)

    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.idxFile)
        hash = 19 * hash + this.id
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> return false
            else -> return when {
                javaClass != other.javaClass -> false
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    when {
                        idxFile != referenceTable.idxFile -> false
                        id != referenceTable.id -> false
                        sector != referenceTable.sector -> false
                        length != referenceTable.length -> false
                        else -> true
                    }
                }
            }
        }
    }
}