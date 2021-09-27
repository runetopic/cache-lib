package com.runetopic.cache.store.storage.impl

import com.github.michaelbull.logging.InlineLogger
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
        val cores = Runtime.getRuntime().availableProcessors()
        var pool: ExecutorService? = null
        if (cores > 2) {
            pool = Executors.newFixedThreadPool(if (cores > 8) 4 else 2)
        }
        val latch = CountDownLatch(masterIdxFile.validIndexCount())

        (0 until masterIdxFile.validIndexCount()).forEach {
            val referenceTable = masterIdxFile.loadReferenceTable(it)
            indexReferenceTables.add(referenceTable)
            idxFiles.add(getIdxFile(it))

            val indexTable = indexReferenceTables.find { id -> id.id == it }!!
            if (indexTable.exists().not()) {
                store.addIndex(Js5Index.default(it))
                latch.countDown()
                return@forEach
            }
            val datTable = datFile.readReferenceTable(masterIdxFile.id(), indexTable)

            pool?.let { service ->
                service.execute {
                    store.addIndex(loadIndex(indexTable, it, ByteBuffer.wrap(datTable).whirlpool(), datTable))
                    latch.countDown()
                }
            } ?: run {
                store.addIndex(loadIndex(indexTable, it, ByteBuffer.wrap(datTable).whirlpool(), datTable))
                latch.countDown()
            }
        }
        latch.await(60, TimeUnit.SECONDS)
        pool?.shutdown()
        logger.debug { "Loaded ${idxFiles.size} indices." }
    }

    override fun loadIndex(table: ReferenceTable, indexId: Int, whirlpool: ByteArray, referenceTable: ByteArray): Js5Index {
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