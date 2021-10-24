package com.runetopic.cache.store.storage.js5

import com.runetopic.cache.extension.Variable
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * @author Jordan Abraham
 */
class Js5IndexTest {

    @Test
    fun `test nameHashes`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val buffer = ByteBuffer.wrap(Random.nextBytes(37 * Int.SIZE_BYTES))

        val decodedNameHashes = decodeGroupNameHashes(
            55,
            37,
            true,
            groupIds,
            buffer
        )

        val encodedNameHashes = encodeGroupNameHashes(
            37,
            true,
            groupIds,
            decodedNameHashes
        )

        assertTrue(buffer.array().contentEquals(encodedNameHashes.array()))
    }

    @Test
    fun `test crcs`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val buffer = ByteBuffer.wrap(Random.nextBytes(37 * Int.SIZE_BYTES))

        val decodedCrcs = decodeGroupCrcs(
            55,
            37,
            groupIds,
            buffer
        )

        val encodedCrcs = encodeGroupCrcs(
            37,
            groupIds,
            decodedCrcs
        )

        assertTrue(buffer.array().contentEquals(encodedCrcs.array()))
    }

    @Test
    fun `test whirlpools`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val buffer = ByteBuffer.wrap(Random.nextBytes(37 * 64))

        val decodedWhirlpools = decodeGroupWhirlpools(
            55,
            true,
            37,
            buffer,
            groupIds
        )

        val encodedWhirlpools = encodeGroupWhirlpools(
            37,
            groupIds,
            true,
            decodedWhirlpools
        )

        assertTrue(buffer.array().contentEquals(encodedWhirlpools.array()))
    }

    @Test
    fun `test revisions`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val buffer = ByteBuffer.wrap(Random.nextBytes(37 * Int.SIZE_BYTES))

        val decodedRevisions = decodeGroupRevisions(
            55,
            37,
            groupIds,
            buffer
        )

        val encodedRevisions = encodeGroupRevisions(
            37,
            groupIds,
            decodedRevisions
        )

        assertTrue(buffer.array().contentEquals(encodedRevisions.array()))
    }

    @Test
    fun `test group file ids`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val buffer = ByteBuffer.wrap(Random.nextBytes(37 * Short.SIZE_BYTES))

        val decodedFileIds = decodeGroupFileIds(
            55,
            37,
            groupIds,
            buffer,
            6
        )

        val encodedFileIds = encodeGroupFileIds(
            37,
            groupIds,
            6,
            decodedFileIds
        )

        assertTrue(buffer.array().contentEquals(encodedFileIds.array()))
    }

    @Test
    fun `test group file ids protocol 7`() {
        val groupIds = intArrayOf(1, 2, 3, 4, 5, 7, 11, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 54)
        val groupFileIds = intArrayOf(0, 166, 397, 652, 240, 610, 0, 337, 0, 0, 0, 1405, 0, 0, 0, 331, 2135, 0, 1244, 1413, 103, 9, 1265, 269, 11, 394, 1790, 0, 0, 0, 0, 8, 2024, 178, 100, 191, 1071, 28, 1, 2, 4, 360, 115, 236, 404, 47, 28, 21, 1, 0, 0, 0, 0, 0, 141)

        var capacity = 0
        (0 until 37).forEach {
            capacity += Variable.asSizeBytes(7, groupFileIds[groupIds[it]])
        }
        val buffer = ByteBuffer.allocate(capacity)

        val decodedFileIds = decodeGroupFileIds(
            55,
            37,
            groupIds,
            buffer,
            7
        )

        val encodedFileIds = encodeGroupFileIds(
            37,
            groupIds,
            7,
            decodedFileIds
        )

        assertTrue(buffer.array().contentEquals(encodedFileIds.array()))
    }
}