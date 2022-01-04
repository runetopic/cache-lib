package com.runetopic.loader.index.loc

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
class LocEntryProvider : IEntryProvider<LocEntryType> {

    private val builder = LocEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): LocEntryType = builder.mapTypes.elementAt(id)
    override fun size(): Int = builder.mapTypes.size
    override fun collect(): Set<LocEntryType> = builder.mapTypes
}
