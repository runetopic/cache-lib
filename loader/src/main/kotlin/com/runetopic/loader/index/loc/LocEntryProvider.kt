package com.runetopic.loader.index.loc

import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class LocEntryProvider : IEntryProvider<LocEntryType> {

    private val builder = LocEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): LocEntryType {
        return builder.mapTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.mapTypes.size
    }

    override fun collect(): Set<LocEntryType> {
        return builder.mapTypes
    }
}