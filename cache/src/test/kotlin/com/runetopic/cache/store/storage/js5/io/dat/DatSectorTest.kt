package com.runetopic.cache.store.storage.js5.io.dat

import com.runetopic.cache.store.storage.js5.io.dat.sector.DatIndexSector
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.ByteBuffer
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
    fun `test ids protocol 6`() {
        val count = Random.nextInt(10..50)
        val groupIds = IntStream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toArray()
        val protocol = 6

        val indexSector = mockk<DatIndexSector>()
        every { indexSector.decodeGroupIds(any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeGroupIds(any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(groupIds.sumOf { (if (protocol >= 7) if (it >= Short.MAX_VALUE) 4 else 2 else 2).toInt() }))

        val decoded = indexSector.decodeGroupIds(count, buffer, protocol)
        val encoded = indexSector.encodeGroupIds(count, protocol, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        println(buffer.array().contentToString())
        println(encoded.array().contentToString())

        verify(exactly = 1) { indexSector.decodeGroupIds(count, buffer, protocol) }
        verify(exactly = 1) { indexSector.encodeGroupIds(count, protocol, decoded) }
        confirmVerified(indexSector)
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
        every { indexSector.decodeFileNameHashes(any(), any(), any(), any(), any(), any()) } answers { callOriginal() }
        every { indexSector.encodeFileNameHashes(any(), any(), any(), any(), any()) } answers { callOriginal() }

        val buffer = ByteBuffer.wrap(Random.nextBytes(validFileIds.sum() * Int.SIZE_BYTES))

        val decoded = indexSector.decodeFileNameHashes(maxGroupId, validFileIds, count, groupIds, buffer, true)
        val encoded = indexSector.encodeFileNameHashes(count, groupIds, validFileIds, true, decoded)

        assertEquals(
            buffer.array().contentToString(),
            encoded.array().contentToString()
        )

        verify(exactly = 1) { indexSector.decodeFileNameHashes(maxGroupId, validFileIds, count, groupIds, buffer, true) }
        verify(exactly = 1) { indexSector.encodeFileNameHashes(count, groupIds, validFileIds, true, decoded) }
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