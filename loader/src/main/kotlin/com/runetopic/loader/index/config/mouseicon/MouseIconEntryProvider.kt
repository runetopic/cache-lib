package com.runetopic.loader.index.config.mouseicon

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class MouseIconEntryProvider : IEntryProvider<MouseIconEntryType> {

    private val builder = MouseIconEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): MouseIconEntryType = builder.mouseIconTypes.elementAt(id)
    override fun size(): Int = builder.mouseIconTypes.size
    override fun collect(): Set<MouseIconEntryType> = builder.mouseIconTypes
}
