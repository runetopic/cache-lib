package com.xlite.loader.group.map

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class MapEntryProvider : IEntryProvider<MapEntryType> {

    private val builder = MapEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): MapEntryType {
        return builder.mapTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.mapTypes.size
    }

    override fun collect(): Set<MapEntryType> {
        return builder.mapTypes
    }
}