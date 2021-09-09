package com.xlite.loader.type.config.struct

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class StructEntryProvider : IEntryProvider<StructEntryType> {

    private val builder = StructEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): StructEntryType {
        return builder.structTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.structTypes.size
    }

    override fun collect(): Set<StructEntryType> {
        return builder.structTypes
    }
}