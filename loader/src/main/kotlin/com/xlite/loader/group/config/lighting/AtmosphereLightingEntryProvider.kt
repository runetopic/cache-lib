package com.xlite.loader.group.config.lighting

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class AtmosphereLightingEntryProvider : IEntryProvider<AtmosphereLightingEntryType> {

    private val builder = AtmosphereLightingEntryTypeBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): AtmosphereLightingEntryType {
        return builder.atmosphereTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.atmosphereTypes.size
    }

    override fun collect(): Set<AtmosphereLightingEntryType> {
        return builder.atmosphereTypes
    }
}