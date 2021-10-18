package com.runetopic.cache.store.storage.js5

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.codec.ContainerCodec
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.Js5Store
import com.runetopic.cache.store.storage.IStorage
import com.runetopic.cache.store.storage.js5.impl.DatFile
import com.runetopic.cache.store.storage.js5.impl.IdxFile
import com.runetopic.cryptography.toWhirlpool
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
@OptIn(ExperimentalPathApi::class)
internal class Js5DiskStorage(
    private val path: Path,
    private val parallel: Boolean
) : IStorage {
    private var masterIdxFile: IIdxFile
    private var datFile: IDatFile
    private var idxFiles: ArrayList<IdxFile> = arrayListOf()
    private val logger = InlineLogger()

    init {
        val masterIndexFile = Path.of("${path}/${Constants.MAIN_FILE_255}")

        if (masterIndexFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_255} in directory ${path}/${Constants.MAIN_FILE_255}")
        }

        val datFile = Path.of("${path}/${Constants.MAIN_FILE_DAT}")

        if (datFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_DAT} in directory ${path}/${Constants.MAIN_FILE_DAT}")
        }

        this.masterIdxFile = IdxFile(Constants.MASTER_INDEX_ID, masterIndexFile)
        this.datFile = DatFile(datFile)
    }

    override fun init(store: Js5Store) {
        logger.debug { "Opening $path for js5 indexes." }

        if (parallel) {
            val latch = CountDownLatch(masterIdxFile.validIndexCount())
            val threads = Runtime.getRuntime().availableProcessors()
            val pool = Executors.newFixedThreadPool(if (threads >= 16) 8 else if (threads >= 8) 4 else 2)
            (0 until masterIdxFile.validIndexCount()).forEach {
                pool.execute {
                    open(it, store)
                    latch.countDown()
                }
            }
            latch.await()
            pool.shutdown()
        } else {
            (0 until masterIdxFile.validIndexCount()).forEach { open(it, store) }
        }
        logger.debug { "Opened ${idxFiles.size} js5 indexes." }
    }

    override fun open(indexId: Int, store: Js5Store) {
        val indexTable = masterIdxFile.loadReferenceTable(indexId)
        idxFiles.add(getIdxFile(indexId))

        if (indexTable.exists().not()) {
            store.addIndex(Js5Index.default(indexId))
            return
        }
        val indexDatTable = datFile.readReferenceTable(masterIdxFile.id(), indexTable)
        store.addIndex(indexTable.loadIndex(datFile, getIdxFile(indexId), indexDatTable.toWhirlpool(), ContainerCodec.decompress(indexDatTable)))
    }

    override fun loadMasterReferenceTable(groupId: Int): ByteArray {
        return datFile.readReferenceTable(Constants.MASTER_INDEX_ID, masterIdxFile.loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: Index, groupId: Int): ByteArray {
        return datFile.readReferenceTable(index.getId(), getIdxFile(index.getId()).loadReferenceTable(groupId))
    }

    override fun loadReferenceTable(index: Index, groupName: String): ByteArray {
        val group = index.getGroup(groupName)
        if (group.getData().isEmpty()) return group.getData()
        return datFile.readReferenceTable(index.getId(), getIdxFile(index.getId()).loadReferenceTable(group.getId()))
    }

    @Synchronized
    private fun getIdxFile(id: Int): IdxFile {
        idxFiles.find { it.id() == id }?.let { return it }
        return IdxFile(id, Path.of("$path/${Constants.MAIN_FILE_IDX}${id}"))
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