package com.runetopic.loader.index.config.param

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ParamEntryProvider : IEntryProvider<ParamEntryType> {

    private val builder = ParamEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): ParamEntryType = builder.paramTypes.elementAt(id)
    override fun size(): Int = builder.paramTypes.size
    override fun collect(): Set<ParamEntryType> = builder.paramTypes
}
