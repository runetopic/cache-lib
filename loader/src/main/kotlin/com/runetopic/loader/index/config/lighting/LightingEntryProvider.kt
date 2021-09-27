package com.runetopic.loader.index.config.lighting

import com.runetopic.cache.store.storage.js5.Js5Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class LightingEntryProvider : IEntryProvider<LightingEntryType> {

    private val builder = LightingEntryBuilder()

    override fun load(store: Js5Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): LightingEntryType {
        return builder.lightings.elementAt(id)
    }

    override fun size(): Int {
        return builder.lightings.size
    }

    override fun collect(): Set<LightingEntryType> {
        return builder.lightings
    }
}