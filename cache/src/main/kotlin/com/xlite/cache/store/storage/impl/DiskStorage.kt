package com.xlite.cache.store.storage.impl

import com.github.michaelbull.logging.InlineLogger
import com.xlite.cache.Js5File
import com.xlite.cache.Js5FileEntry
import com.xlite.cache.Js5Group
import com.xlite.cache.extension.whirlpool
import com.xlite.cache.store.Store
import com.xlite.cache.store.Constants
import com.xlite.cache.store.fs.IDatFile
import com.xlite.cache.store.fs.IIdxFile
import com.xlite.cache.store.fs.impl.DatFile
import com.xlite.cache.store.fs.impl.IdxFile
import com.xlite.cache.store.storage.IStorage
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
            idxFiles.add(getIdxFile(it))
            store.addGroup(loadGroup(it))
        }
        logger.debug { "Loaded ${idxFiles.size} indices." }
    }

    override fun loadGroup(id: Int): Js5Group {
        val table = masterIdxFile.loadReferenceTable(id)
        val groupData = datFile.readReferenceTable(masterIdxFile.id(), table)
        val whirlpool = ByteBuffer.wrap(groupData).whirlpool()
        return table.loadGroup(id, whirlpool, groupData)
    }

    override fun loadFile(group: Js5Group, fileName: String): Js5File? {
        val file = group.getFile(fileName)
        file?.load(datFile, getIdxFile(file.groupId))
        return file
    }

    override fun loadFile(group: Js5Group, fileId: Int): Js5File? {
        val file = group.getFile(fileId)
        file?.load(datFile, getIdxFile(file.groupId))
        return file
    }

    override fun loadEntry(group: Js5Group, fileId: Int, entryId: Int): Js5FileEntry {
        val js5File = loadFile(group, fileId)
        js5File?.loadFileEntriesData(entryId, js5File)
        return js5File?.entries?.find { it.entryId == entryId } ?: Js5FileEntry(fileId, entryId, -1, byteArrayOf(0))
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
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}