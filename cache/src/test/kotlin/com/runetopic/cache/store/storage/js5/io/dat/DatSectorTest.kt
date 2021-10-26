package com.runetopic.cache.store.storage.js5.io.dat

import com.runetopic.cache.store.storage.js5.io.dat.sector.DatIndexSector
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.ByteBuffer
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
    fun `test nameHashes`() {
        val count = Random.nextInt(10..50)
        val groupIds = Stream.generate { Random.nextInt(count..count * 2) }.distinct().limit(count.toLong()).sorted().toList().toIntArray()
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