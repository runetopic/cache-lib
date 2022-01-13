package com.runetopic.cache.store.storage.js5.io.dat

import com.runetopic.cache.store.storage.js5.io.dat.sector.DatIndexSector
import com.runetopic.cache.store.storage.js5.io.idx.IdxFile
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.stream.IntStream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Jordan Abraham
 */
class DatSectorTest {

    @Test
    fun `test decode and encode`() {
        val indexSector = mockk<DatIndexSector>()

        every { indexSector.datFile } returns DatFile(Path.of("src", "test", "resources", "./main_file_cache.dat2"))
        every { indexSector.idxFile } returns IdxFile(10, Path.of("src", "test", "resources", "./main_file_cache.idx10"))
        every { indexSector.data } returns byteArrayOf(0, 0, 0, 0, 30, 6, 0, 0, 0, 5, 1, 0, 1, 0, 1, 74, -4, 115, -83, 120, 17, 126, 78, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0)

        every { indexSector.decode() } answers { callOriginal() }
        every { indexSector.decodeGroupIds(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupNameHashes(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupCrcs(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupWhirlpools(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupRevisions(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupFileSizes(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupFileIds(any(), any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.decodeGroupFileNameHashes(any(), any(), any(), any(), any(), any()) } answers { callOriginal() }

        every { indexSector.calc(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encode(any()) } answers { callOriginal() }
        every { indexSector.encodeGroupIds(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupNameHashes(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupCrcs(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupWhirlpools(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupRevisions(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileSizes(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileIds(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileNameHashes(any(), any(), any(), any(), any()) } answers { callOriginal() }

        val decoded = indexSector.decode()
        val encoded = indexSector.encode(decoded)

        assertEquals(
            indexSector.data.contentToString(),
            encoded.contentToString()
        )

        verify(exactly = 1) { indexSector.decode() }
        verify(atLeast = 1) { indexSector.decodeGroupIds(any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupNameHashes(any(), any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupCrcs(any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupWhirlpools(any(), any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupRevisions(any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupFileSizes(any(), any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupFileIds(any(), any(), any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.decodeGroupFileNameHashes(any(), any(), any(), any(), any(), any()) }

        verify(exactly = 1) { indexSector.encode(decoded) }
        verify(atLeast = 1) { indexSector.calc(any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupIds(any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupNameHashes(any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupCrcs(any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupWhirlpools(any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupRevisions(any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupFileSizes(any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupFileIds(any(), any(), any(), any(), any()) }
        verify(atLeast = 1) { indexSector.encodeGroupFileNameHashes(any(), any(), any(), any(), any()) }

        verify(atLeast = 1) { indexSector.datFile }
        verify(atLeast = 1) { indexSector.idxFile }
        verify(atLeast = 1) { indexSector.data }

        confirmVerified(indexSector)
    }

    @Test
    fun `test file ids protocol 6`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
        val groupFileSizes = IntArray(maxGroupId) { if (it in groupIds) Random.nextInt(1..10) else 0 }
        val protocol = 7

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupFileIds(any(), any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileIds(any(), any(), any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(groupFileSizes.sumOf { it * Short.SIZE_BYTES }))

        val decoded = indexSector.decodeGroupFileIds(maxGroupId, groupFileSizes, count, groupIds, buffer, protocol)
        val encoded = indexSector.encodeGroupFileIds(count, groupIds, protocol, groupFileSizes, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupFileIds(maxGroupId, groupFileSizes, count, groupIds, buffer, protocol) }
        verify(exactly = 1) { indexSector.encodeGroupFileIds(count, groupIds, protocol, groupFileSizes, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test ids protocol 6`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val protocol = 7

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupIds(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupIds(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.calc(any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(indexSector.calc(count, groupIds, protocol)))

        val decoded = indexSector.decodeGroupIds(count, buffer, protocol)
        val encoded = indexSector.encodeGroupIds(count, protocol, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupIds(count, buffer, protocol) }
        verify(exactly = 1) { indexSector.encodeGroupIds(count, protocol, decoded) }
        verify(atLeast = 1) { indexSector.calc(count, groupIds, protocol) }
        // confirmVerified(indexSector)
    }

    @Test
    fun `test fileSizes protocol 6`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
        val protocol = 6

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupFileSizes(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileSizes(any(), any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(groupIds.sumOf { (if (protocol >= 7) if (it >= Short.MAX_VALUE) 4 else 2 else 2).toInt() }))

        val decoded = indexSector.decodeGroupFileSizes(maxGroupId, count, groupIds, buffer, protocol)
        val encoded = indexSector.encodeGroupFileSizes(count, groupIds, protocol, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupFileSizes(maxGroupId, count, groupIds, buffer, protocol) }
        verify(exactly = 1) { indexSector.encodeGroupFileSizes(count, groupIds, protocol, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test file nameHashes`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1
        val validFileIds = IntArray(maxGroupId) { if (it in groupIds) Random.nextInt(1..10) else 0 }

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupFileNameHashes(any(), any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupFileNameHashes(any(), any(), any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(validFileIds.sum() * Int.SIZE_BYTES))

        val decoded = indexSector.decodeGroupFileNameHashes(maxGroupId, validFileIds, count, groupIds, buffer, true)
        val encoded = indexSector.encodeGroupFileNameHashes(count, groupIds, validFileIds, true, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupFileNameHashes(maxGroupId, validFileIds, count, groupIds, buffer, true) }
        verify(exactly = 1) { indexSector.encodeGroupFileNameHashes(count, groupIds, validFileIds, true, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test whirlpools`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1

        val buffer = ByteBuffer.wrap(Random.nextBytes(count * 64))

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupWhirlpools(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupWhirlpools(any(), any(), any(), any()) } answers { callOriginal() }

        val decoded = indexSector.decodeGroupWhirlpools(maxGroupId, true, count, buffer, groupIds)
        val encoded = indexSector.encodeGroupWhirlpools(count, groupIds, true, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupWhirlpools(maxGroupId, true, count, buffer, groupIds) }
        verify(exactly = 1) { indexSector.encodeGroupWhirlpools(count, groupIds, true, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test crcs`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1

        val buffer = ByteBuffer.wrap(Random.nextBytes(count * Int.SIZE_BYTES))

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupCrcs(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupCrcs(any(), any(), any()) } answers { callOriginal() }

        val decoded = indexSector.decodeGroupCrcs(maxGroupId, count, groupIds, buffer)
        val encoded = indexSector.encodeGroupCrcs(count, groupIds, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupCrcs(maxGroupId, count, groupIds, buffer) }
        verify(exactly = 1) { indexSector.encodeGroupCrcs(count, groupIds, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test nameHashes`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: -1) + 1

        val buffer = ByteBuffer.wrap(Random.nextBytes(count * Int.SIZE_BYTES))

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupNameHashes(any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupNameHashes(any(), any(), any(), any()) } answers { callOriginal() }

        val decoded = indexSector.decodeGroupNameHashes(maxGroupId, count, true, groupIds, buffer)
        val encoded = indexSector.encodeGroupNameHashes(count, true, groupIds, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupNameHashes(maxGroupId, count, true, groupIds, buffer) }
        verify(exactly = 1) { indexSector.encodeGroupNameHashes(count, true, groupIds, decoded) }
        confirmVerified(indexSector)
    }

    @Test
    fun `test revisions`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val maxGroupId = (groupIds.maxOrNull() ?: 0) + 1

        val buffer = ByteBuffer.wrap(Random.nextBytes(count * Int.SIZE_BYTES))

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupRevisions(any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupRevisions(any(), any(), any()) } answers { callOriginal() }

        val decoded = indexSector.decodeGroupRevisions(maxGroupId, count, groupIds, buffer)
        val encoded = indexSector.encodeGroupRevisions(count, groupIds, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeGroupRevisions(maxGroupId, count, groupIds, buffer) }
        verify(exactly = 1) { indexSector.encodeGroupRevisions(count, groupIds, decoded) }
        confirmVerified(indexSector)
    }
}
