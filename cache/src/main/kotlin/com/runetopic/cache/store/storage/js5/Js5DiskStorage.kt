package com.runetopic.cache.store.storage.js5

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.Js5Store
import com.runetopic.cache.store.storage.IStorage
import com.runetopic.cache.store.storage.js5.io.dat.DatFile
import com.runetopic.cache.store.storage.js5.io.dat.IDatFile
import com.runetopic.cache.store.storage.js5.io.dat.sector.DatIndexSector
import com.runetopic.cache.store.storage.js5.io.idx.IIdxFile
import com.runetopic.cache.store.storage.js5.io.idx.IdxFile
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList
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
    private var idxFiles = CopyOnWriteArrayList<IdxFile>()
    private val logger = InlineLogger()

    init {
        val masterIndexFile = Path.of("$path/${Constants.MAIN_FILE_255}")

        if (masterIndexFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_255} in directory $path/${Constants.MAIN_FILE_255}")
        }

        val datFile = Path.of("$path/${Constants.MAIN_FILE_DAT}")

        if (datFile.exists().not()) {
            throw FileNotFoundException("Missing ${Constants.MAIN_FILE_DAT} in directory $path/${Constants.MAIN_FILE_DAT}")
        }

        this.masterIdxFile = IdxFile(Constants.MASTER_INDEX_ID, masterIndexFile)
        this.datFile = DatFile(datFile)
    }

    override fun open(store: Js5Store) {
        logger.debug { "Opening $path for js5 indexes." }

        if (parallel) {
            val latch = CountDownLatch(masterIdxFile.validIndexCount())
            val threads = Runtime.getRuntime().availableProcessors()
            val pool = Executors.newFixedThreadPool(if (threads >= 16) 8 else if (threads >= 8) 4 else 2)
            repeat(masterIdxFile.validIndexCount()) {
                pool.execute {
                    read(it, store)
                    latch.countDown()
                }
            }
            latch.await()
            pool.shutdown()
        } else { repeat(masterIdxFile.validIndexCount()) { read(it, store) } }
        logger.debug { "Opened ${idxFiles.size} js5 indexes. (Allocated ${((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)}MB)." }
    }

    override fun read(indexId: Int, store: Js5Store) {
        val indexReferenceTable = masterIdxFile.decode(indexId)
        idxFiles.add(getIdxFile(indexId))

        if (indexReferenceTable.exists().not()) {
            store.addIndex(Index.default(indexId))
            return
        }
        val data = datFile.decode(masterIdxFile.id(), indexReferenceTable)
        store.addIndex(DatIndexSector(datFile, getIdxFile(indexId), data).decode())
    }

    override fun write(indexId: Int, store: Js5Store) {
        val idk = masterIdxFile.encode(byteArrayOf())
        val data = datFile.encode(byteArrayOf())
        val index = store.index(indexId)
        // val idk2 = DatIndexSector(datFile, getIdxFile(indexId), data).encode(index)
    }

    override fun loadMasterReferenceTable(groupId: Int): ByteArray = datFile.decode(Constants.MASTER_INDEX_ID, masterIdxFile.decode(groupId))

    override fun loadReferenceTable(index: Index, groupId: Int): ByteArray = datFile.decode(index.id, getIdxFile(index.id).decode(groupId))

    override fun loadReferenceTable(index: Index, groupName: String): ByteArray = index.group(groupName).let {
        if (it.data.isEmpty()) return it.data
        else datFile.decode(index.id, getIdxFile(index.id).decode(it.id))
    }

    private fun getIdxFile(id: Int): IdxFile = idxFiles.find { it.id() == id } ?: IdxFile(id, Path.of("$path/${Constants.MAIN_FILE_IDX}$id"))

    override fun close() {
        masterIdxFile.close()
        datFile.close()
        idxFiles.forEach { it.close() }
    }

    override fun flush() {
        TODO("Not yet implemented")
    }
}
