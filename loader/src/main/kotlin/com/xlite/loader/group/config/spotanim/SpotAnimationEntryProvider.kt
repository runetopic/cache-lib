package com.xlite.loader.group.config.spotanim

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class SpotAnimationEntryProvider : IEntryProvider<SpotAnimationEntryType> {

    private val builder = SpotAnimationEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): SpotAnimationEntryType {
        return builder.spotAnimations.elementAt(id)
    }

    override fun size(): Int {
        return builder.spotAnimations.size
    }

    override fun collect(): Set<SpotAnimationEntryType> {
        return builder.spotAnimations
    }
}