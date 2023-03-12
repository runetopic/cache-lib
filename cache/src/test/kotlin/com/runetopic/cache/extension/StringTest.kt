package com.runetopic.cache.extension

import kotlin.test.Test

/**
 * @author Jordan Abraham
 */
class StringTest {

    @Test
    fun `test string name hash`() {
        val name = "hello world"
        val hash = name.hashed()
        assert(hash == 1794106052)
    }
}
