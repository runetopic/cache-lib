package com.xlite.loader

import com.xlite.loader.group.config.spotanim.SpotAnimationEntryProvider
import com.xlite.loader.group.config.struct.StructEntryProvider

private val spotAnimationEntryProvider = SpotAnimationEntryProvider()
private val structEntryProvider = StructEntryProvider()

fun spotAnimations(): SpotAnimationEntryProvider {
    return spotAnimationEntryProvider
}

fun structs(): StructEntryProvider {
    return structEntryProvider
}