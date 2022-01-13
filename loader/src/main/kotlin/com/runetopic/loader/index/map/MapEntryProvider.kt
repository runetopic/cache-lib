package com.runetopic.loader.index.map

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class MapEntryProvider : IEntryProvider<MapEntryType> {

    private val builder = MapEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): MapEntryType = builder.mapTypes.elementAt(id)
    override fun size(): Int = builder.mapTypes.size
    override fun collect(): Set<MapEntryType> = builder.mapTypes
}
