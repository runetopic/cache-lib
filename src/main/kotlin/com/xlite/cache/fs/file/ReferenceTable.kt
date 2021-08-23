package com.xlite.cache.fs.file

import com.xlite.cache.fs.file.impl.IndexFile
import java.util.*

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class ReferenceTable(
    val indexFile: IndexFile,
    val archiveId: Int,
    val sector: Int,
    val length: Int
) {
    override fun hashCode(): Int {
        var hash = 7
        hash = 19 * hash + Objects.hashCode(this.indexFile)
        hash = 19 * hash + this.archiveId
        hash = 19 * hash + this.sector
        hash = 19 * hash + this.length
        return hash
    }

    override fun equals(other: Any?): Boolean {
        when (other) {
            null -> {
                return false
            }
            else -> when {
                javaClass != other.javaClass -> {
                    return false
                }
                else -> {
                    val referenceTable: ReferenceTable = other as ReferenceTable
                    return when {
                        indexFile != referenceTable.indexFile -> {
                            false
                        }
                        archiveId != referenceTable.archiveId -> {
                            false
                        }
                        sector != referenceTable.sector -> {
                            false
                        }
                        this.length != referenceTable.length -> {
                            false
                        }
                        else -> true
                    }
                }
            }
        }
    }
}