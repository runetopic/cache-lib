package com.runetopic.loader.index.obj

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ObjEntryProvider : IEntryProvider<ObjEntryType> {

    private val builder = ObjEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): ObjEntryType = builder.objs.elementAt(id)
    override fun size(): Int = builder.objs.size
    override fun collect(): Set<ObjEntryType> = builder.objs
}