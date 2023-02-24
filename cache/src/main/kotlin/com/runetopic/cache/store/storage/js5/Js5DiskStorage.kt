package com.runetopic.cache.store.storage.js5

import com.github.michaelbull.logging.InlineLogger
import com.runetopic.cache.extension.decompress
import com.runetopic.cache.hierarchy.index.Index
import com.runetopic.cache.store.Constants
import com.runetopic.cache.store.Js5Store
import com.runetopic.cache.store.storage.Storage
import com.runetopic.cache.store.storage.js5.impl.DatFile
import com.runetopic.cache.store.storage.js5.impl.IdxFile
import com.runetopic.cryptography.toWhirlpool
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.io.path.exists

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 *
 * @author Jordan Abraham
 */
internal class Js5DiskStorage(
    private val path: Path,
    private val parallel: Boolean
) : Storage {
    private val masterIdxFile: IIdxFile
    private val datFile: IDatFile
    private val idxFiles = CopyOnWriteArrayList<IdxFile>()
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

    override fun init(store: Js5Store) {
        logger.debug { "Opening $path for js5 indexes." }

        if (parallel) {
            val latch = CountDownLatch(masterIdxFile.validIndexCount())
            val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            repeat(masterIdxFile.validIndexCount()) {
                pool.execute {
                    open(it, store)
                    latch.countDown()
                }
            }
            latch.await()
            pool.shutdown()
        } else {
            repeat(masterIdxFile.validIndexCount()) {
                open(it, store)
            }
        }
        logger.debug { "Opened ${idxFiles.size} js5 indexes. (Allocated ${((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)}MB)." }
    }

    override fun open(indexId: Int, store: Js5Store) {
        val indexTable = masterIdxFile.loadReferenceTable(indexId)
        idxFiles.add(getIdxFile(indexId))

        if (indexTable.length == 0 && indexTable.sector == 0) {
            // Default this as an index if it can't be created.
            store.addIndex(
                Index(
                    id = indexId,
                    crc = 0,
                    whirlpool = byteArrayOf(),
                    compression = -1,
                    protocol = -1,
                    revision = 0,
                    isNamed = false,
                    groups = arrayOf()
                )
            )
            return
        }
        datFile.readReferenceTable(masterIdxFile.id(), indexTable).let {
            store.addIndex(
                it.decompress().decodeJs5Index(
                    datFile = datFile,
                    idxFile = getIdxFile(indexId),
                    whirlpool = it.toWhirlpool()
                )
            )
        }
    }

    override fun loadMasterReferenceTable(groupId: Int): ByteArray = datFile.readReferenceTable(
        id = Constants.MASTER_INDEX_ID,
        referenceTable = masterIdxFile.loadReferenceTable(groupId)
    )

    override fun loadReferenceTable(index: Index, groupId: Int): ByteArray = datFile.readReferenceTable(
        id = index.id,
        referenceTable = getIdxFile(index.id).loadReferenceTable(groupId)
    )

    override fun loadReferenceTable(index: Index, groupName: String): ByteArray {
        val group = index.group(groupName) ?: return byteArrayOf()
        return datFile.readReferenceTable(
            id = index.id,
            referenceTable = getIdxFile(index.id).loadReferenceTable(group.id)
        )
    }

    private fun getIdxFile(id: Int): IdxFile = idxFiles.find { it.id() == id } ?: IdxFile(id, Path.of("$path/${Constants.MAIN_FILE_IDX}$id"))
}
