package com.runetopic.cache.store.storage.js5.io.dat

import com.runetopic.cache.store.storage.js5.io.dat.sector.DatIndexSector
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.ByteBuffer
import java.util.concurrent.ThreadLocalRandom
import java.util.function.IntSupplier
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Jordan Abraham
 */
class DatSectorTest {

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