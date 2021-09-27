package com.runetopic.loader.index.map

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class MapLocationEntryProvider : IEntryProvider<MapLocationEntryType> {

    private val builder = MapLocationEntryBuilder()

    override fun load(store: Js5Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): MapLocationEntryType {
        return builder.mapTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.mapTypes.size
    }

    override fun collect(): Set<MapLocationEntryType> {
        return builder.mapTypes
    }
}