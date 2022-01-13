package com.runetopic.loader.index.config.lighting

import com.runetopic.loader.IEntryType

data class LightingEntryType(
    private val id: Int = 0,
    var anInt961: Int = 0,
    var anInt962: Int = 0,
    var anInt956: Int = 2048,
    var anInt957: Int = 2048,
) : IEntryType {
    override fun getId(): Int = id
}
