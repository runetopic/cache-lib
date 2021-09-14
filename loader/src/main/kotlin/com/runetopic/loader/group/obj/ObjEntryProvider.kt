package com.runetopic.loader.group.obj

import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ObjEntryProvider : IEntryProvider<ObjEntryType> {

    private val builder = ObjEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): ObjEntryType {
        return builder.objs.elementAt(id)
    }

    override fun size(): Int {
        return builder.objs.size
    }

    override fun collect(): Set<ObjEntryType> {
        return builder.objs
    }
}