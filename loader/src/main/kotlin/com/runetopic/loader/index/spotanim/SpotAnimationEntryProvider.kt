package com.runetopic.loader.index.spotanim

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class SpotAnimationEntryProvider : IEntryProvider<SpotAnimationEntryType> {

    private val builder = SpotAnimationEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): SpotAnimationEntryType = builder.spotAnimations.elementAt(id)
    override fun size(): Int = builder.spotAnimations.size
    override fun collect(): Set<SpotAnimationEntryType> = builder.spotAnimations
}
