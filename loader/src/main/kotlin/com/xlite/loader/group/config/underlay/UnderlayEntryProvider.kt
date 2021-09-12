package com.xlite.loader.group.config.underlay

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class UnderlayEntryProvider : IEntryProvider<UnderlayEntryType> {

    private val builder = UnderlayEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): UnderlayEntryType {
        return builder.underlays.elementAt(id)
    }

    override fun size(): Int {
        return builder.underlays.size
    }

    override fun collect(): Set<UnderlayEntryType> {
        return builder.underlays
    }
}