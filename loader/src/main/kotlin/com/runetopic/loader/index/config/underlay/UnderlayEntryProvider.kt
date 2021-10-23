package com.runetopic.loader.index.config.underlay

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class UnderlayEntryProvider : IEntryProvider<UnderlayEntryType> {

    private val builder = UnderlayEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): UnderlayEntryType = builder.underlays.elementAt(id)
    override fun size(): Int = builder.underlays.size
    override fun collect(): Set<UnderlayEntryType> = builder.underlays
}