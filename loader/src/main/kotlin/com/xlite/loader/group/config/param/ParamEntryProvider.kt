package com.xlite.loader.group.config.param

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class ParamEntryProvider : IEntryProvider<ParamEntryType> {

    private val builder = ParamEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): ParamEntryType {
        return builder.structTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.structTypes.size
    }

    override fun collect(): Set<ParamEntryType> {
        return builder.structTypes
    }
}