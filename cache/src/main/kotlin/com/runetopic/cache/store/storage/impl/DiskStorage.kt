package com.runetopic.cache.store.storage.impl

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.crypto.Whirlpool
import com.runetopic.cache.extension.whirlpool
import com.runetopic.cache.hierarchy.ReferenceTable
import com.runetopic.cache.hierarchy.index.IIndex
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.Store
import com.runetopic.cache.store.js5.IDatFile
import com.runetopic.cache.store.js5.IIdxFile
import com.runetopic.cache.store.js5.impl.DatFile
import com.runetopic.cache.store.js5.impl.IdxFile
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

        this.masterIdxFile = IdxFile(Constants.MASTER_INDEX_ID, masterIndexFile)
        this.datFile = DatFile(datFile)
    }

    override fun init(store: Store) {
        (0 until masterIdxFile.validIndexCount()).forEach {
            val referenceTable = masterIdxFile.loadReferenceTable(it)
            indexReferenceTables.add(referenceTable)
            idxFiles.add(getIdxFile(it))
            store.addIndex(loadIndex(it))
        }
        logger.debug { "Loaded ${idxFiles.size} indices." }
    }

    override fun loadIndex(indexId: Int): Js5Index {
        val table = indexReferenceTables.find { it.id == indexId }!!
        if (table.exists().not()) {
            return Js5Index(indexId, 0, ByteArray(Whirlpool.DIGESTBYTES), -1, -1, 0, false, hashMapOf())
        }
        val referenceTable = datFile.readReferenceTable(masterIdxFile.id(), table)
        val whirlpool = ByteBuffer.wrap(referenceTable).whirlpool()
        return table.loadIndex(datFile, getIdxFile(indexId), whirlpool, referenceTable)
    }

    override fun loadMasterReferenceTable(groupId: Int): ByteArray {
        return datFile.readReferenceTable(Constants.MASTER_INDEX_ID, masterIdxFile.loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: IIndex, groupId: Int): ByteArray {
        return datFile.readReferenceTable(index.getId(), getIdxFile(index.getId()).loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: IIndex, groupName: String): ByteArray {
        val group = index.getGroup(groupName)
        if (group.getData().isEmpty()) return group.getData()
        return datFile.readReferenceTable(index.getId(), getIdxFile(index.getId()).loadReferenceTable(group.getId()))
    }

    private fun getIdxFile(id: Int): IdxFile {
        idxFiles.find { it.id() == id }?.let { return it }
        return IdxFile(id, File("$directory/${Constants.MAIN_FILE_IDX}${id}"))
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