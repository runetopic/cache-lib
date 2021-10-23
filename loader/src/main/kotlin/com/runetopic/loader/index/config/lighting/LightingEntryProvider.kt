package com.runetopic.loader.index.config.lighting

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class LightingEntryProvider : IEntryProvider<LightingEntryType> {

    private val builder = LightingEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): LightingEntryType = builder.lightings.elementAt(id)
    override fun size(): Int = builder.lightings.size
    override fun collect(): Set<LightingEntryType> = builder.lightings
}