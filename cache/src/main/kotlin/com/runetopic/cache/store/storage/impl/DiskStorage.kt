package com.runetopic.cache.store.storage.impl

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.Js5Group
import com.runetopic.cache.Js5File
import com.runetopic.cache.Js5Index
import com.runetopic.cache.ReferenceTable
import com.runetopic.cache.extension.whirlpool
import com.runetopic.cache.store.Store
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.fs.IDatFile
import com.runetopic.cache.store.fs.IIdxFile
import com.runetopic.cache.store.fs.impl.DatFile
import com.runetopic.cache.store.fs.impl.IdxFile
import com.runetopic.cache.store.storage.IStorage
import java.io.File
import java.io.FileNotFoundException
import java.nio.ByteBuffer

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
internal class DiskStorage(
    private val directory: File
) : IStorage {
    private var masterIdxFile: IIdxFile
    private var datFile: IDatFile
    private var idxFiles: ArrayList<IdxFile> = arrayListOf()
    private var indexReferenceTables: ArrayList<ReferenceTable> = arrayListOf()
    private val logger = InlineLogger()

    init {
        val masterIndexFile = File("${directory}/${Constants.MAIN_FILE_255}")

        if (masterIndexFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_255} in directory ${directory}/${Constants.MAIN_FILE_255}")
        }

        val datFile = File("${directory}/${Constants.MAIN_FILE_DAT}")

        if (datFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_DAT} in directory ${directory}/${Constants.MAIN_FILE_DAT}")
        }

        this.masterIdxFile = IdxFile(Constants.MAIN_INDEX_ID, masterIndexFile)
        this.datFile = DatFile(datFile)
    }

    override fun init(store: Store) {
        (0 until masterIdxFile.validIndexCount()).forEach {
            val referenceTable = masterIdxFile.loadReferenceTable(it)
            indexReferenceTables.add(referenceTable)
            getIdxFile(it)?.let { file ->
                idxFiles.add(file)
                store.addIndex(loadIndex(it))
            }
        }
        logger.debug { "Loaded ${idxFiles.size} indices." }
    }

    override fun loadIndex(indexId: Int): Js5Index {
        val table = indexReferenceTables.find { it.id == indexId }!!
        val referenceTable = datFile.readReferenceTable(masterIdxFile.id(), table)
        val whirlpool = ByteBuffer.wrap(referenceTable).whirlpool()
        return table.loadIndex(datFile, getIdxFile(indexId)!!, whirlpool, referenceTable)
    }

    override fun loadGroup(index: Js5Index, groupName: String): Js5Group? {
        return index.getGroup(groupName)
    }

    override fun loadGroup(index: Js5Index, groupId: Int): Js5Group? {
        return index.getGroup(groupId)
    }

    override fun loadFile(index: Js5Index, groupId: Int, fileId: Int): Js5File {
        val group = loadGroup(index, groupId)!!
        val file = group.files.find { it.id == fileId } ?: Js5File(groupId, fileId, -1, byteArrayOf(0))
        //a file not found with have data != null with a byte of 0 which will auto break a loader at opcode 0.
        file.data ?: group.loadFiles(file)
        return file
    }

    override fun loadReferenceTable(index: Js5Index, groupId: Int): ByteArray {
        return datFile.readReferenceTable(index.id, getIdxFile(index.id)!!.loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: Js5Index, groupName: String): ByteArray {
        val group = index.getGroup(groupName) ?: return byteArrayOf()
        return datFile.readReferenceTable(index.id, getIdxFile(index.id)!!.loadReferenceTable(group.groupId))
    }

    private fun getIdxFile(id: Int): IdxFile? {
        val cachedIndexFile = idxFiles.find { it.id() == id }

        if (cachedIndexFile != null) return cachedIndexFile

        val file = File("$directory/${Constants.MAIN_FILE_IDX}${id}")

        if (file.exists().not()) {
            //throw FileNotFoundException("Missing ${Constants.MAIN_FILE_IDX} in directory $directory")
            return null
        }
        return IdxFile(id, file)
    }

    override fun close() {
        masterIdxFile.close()
        datFile.close()
        idxFiles.forEach { it.close() }
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}