package com.xlite.cache.type.obj

import com.displee.cache.index.Index
import com.xlite.cache.type.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ObjEntryProvider : IEntryProvider<ObjEntryType> {

    private val builder = ObjEntryBuilder()

    override fun load(index: Index) {
        builder.build(index)
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