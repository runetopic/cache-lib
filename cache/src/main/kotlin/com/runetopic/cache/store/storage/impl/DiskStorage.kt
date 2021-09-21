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
            if (referenceTable.sector > 0) {
                indexReferenceTables.add(referenceTable)
                idxFiles.add(getIdxFile(it))
                store.addIndex(loadIndex(it))
            }
        }
        logger.debug { "Loaded ${idxFiles.size} indices." }
    }

    override fun loadIndex(indexId: Int): Js5Index {
        val table = indexReferenceTables.find { it.id == indexId }!!
        val referenceTable = datFile.readReferenceTable(masterIdxFile.id(), table)
        val whirlpool = ByteBuffer.wrap(referenceTable).whirlpool()
        return table.loadIndex(indexId, whirlpool, referenceTable)
    }

    override fun loadGroup(index: Js5Index, groupName: String): Js5Group? {
        val file = index.getGroup(groupName)
        index.groups.filter { file?.groupId == it.value.groupId }.keys.firstOrNull()?.let { fileId ->
            file?.loadGroup(datFile, getIdxFile(file.indexId), fileId)
        }
        return file
    }

    override fun loadGroup(index: Js5Index, groupId: Int): Js5Group? {
        val group = index.getGroup(groupId)
        group?.loadGroup(datFile, getIdxFile(group.indexId), groupId)
        return group
    }

    override fun loadFile(index: Js5Index, groupId: Int, fileId: Int): Js5File {
        val group = loadGroup(index, groupId)!!
        group.loadFiles(fileId)
        return group.files.find { it.id == fileId } ?: Js5File(groupId, fileId, -1, byteArrayOf(0))
    }

    override fun loadReferenceTable(index: Js5Index, groupId: Int): ByteArray {
        return datFile.readReferenceTable(index.id, getIdxFile(index.id).loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: Js5Index, groupName: String): ByteArray {
        val file = index.getGroup(groupName)
        val fileId = index.groups.filter { file?.groupId == it.value.groupId }.keys.firstOrNull() ?: return byteArrayOf()
        return datFile.readReferenceTable(index.id, getIdxFile(index.id).loadReferenceTable(fileId))
    }

    private fun getIdxFile(id: Int): IdxFile {
        val cachedIndexFile = idxFiles.find { it.id() == id }

        if (cachedIndexFile != null) return cachedIndexFile

        val file = File("$directory/${Constants.MAIN_FILE_IDX}${id}")

        if (file.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_IDX} in directory $directory")
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