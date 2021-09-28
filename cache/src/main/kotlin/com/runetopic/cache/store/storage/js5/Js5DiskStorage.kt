package com.runetopic.cache.store.storage.js5

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.codec.ContainerCodec
import com.runetopic.cache.extension.whirlpool
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.hierarchy.index.Js5Index
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.Js5Store
import com.runetopic.cache.store.storage.IStorage
import com.runetopic.cache.store.storage.js5.impl.DatFile
import com.runetopic.cache.store.storage.js5.impl.IdxFile
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
@OptIn(ExperimentalPathApi::class)
internal class Js5DiskStorage(
    private val path: Path
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
        val cores = Runtime.getRuntime().availableProcessors()
        var pool: ExecutorService? = null
        if (cores > 4) {
            pool = Executors.newFixedThreadPool(if (cores > 8) 4 else 2)
        }
        val latch = CountDownLatch(masterIdxFile.validIndexCount())

        (0 until masterIdxFile.validIndexCount()).forEach { indexId ->
            pool?.let { it.execute { store(indexId, store, latch) } }
                ?: run { store(indexId, store, latch) }
        }

        latch.await(60, TimeUnit.SECONDS)
        pool?.shutdown()
        logger.debug { "Loaded ${idxFiles.size} indexes." }
    }

    override fun store(indexId: Int, store: Js5Store, latch: CountDownLatch) {
        val indexTable = masterIdxFile.loadReferenceTable(indexId)
        idxFiles.add(getIdxFile(indexId))

        if (indexTable.exists().not()) {
            store.addIndex(Js5Index.default(indexId))
            latch.countDown()
            return
        }

        val indexDatTable = datFile.readReferenceTable(masterIdxFile.id(), indexTable)
        store.addIndex(indexTable.loadIndex(datFile, getIdxFile(indexId), ByteBuffer.wrap(indexDatTable).whirlpool(), ContainerCodec.decompress(indexDatTable)))
        latch.countDown()
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