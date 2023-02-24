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
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.exitProcess

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
    private val masterIdxFile = IdxFile(Constants.MASTER_INDEX_ID, Path.of("$path/${Constants.MAIN_FILE_255}"))
    private val datFile = DatFile(Path.of("$path/${Constants.MAIN_FILE_DAT}"))
    private val idxFiles = arrayOfNulls<IdxFile>(validIndexCount())
    private val logger = InlineLogger()

    override fun init(store: Js5Store) = try {
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
    } catch (exception: Exception) {
        exitProcess(-1)
    }

    override fun open(indexId: Int, store: Js5Store) {
        val indexTable = masterIdxFile.loadReferenceTable(indexId)
        val idxFile = getIdxFile(indexId).also { idxFiles[indexId] = it }

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
                    idxFile = idxFile,
                    whirlpool = it.toWhirlpool()
                )
            )
        }
    }

    override fun loadMasterReferenceTable(groupId: Int): ByteArray = datFile.readReferenceTable(
        id = Constants.MASTER_INDEX_ID,
        referenceTable = masterIdxFile.loadReferenceTable(groupId)
    )

    override fun validIndexCount(): Int = masterIdxFile.validIndexCount()

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

    private fun getIdxFile(id: Int): IdxFile = idxFiles[id] ?: IdxFile(id, Path.of("$path/${Constants.MAIN_FILE_IDX}$id"))
}
