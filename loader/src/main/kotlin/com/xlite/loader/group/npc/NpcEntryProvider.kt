package com.xlite.loader.group.npc

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class NpcEntryProvider: IEntryProvider<NpcEntryType> {

    private val builder = NpcEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): NpcEntryType {
        return builder.npcs.elementAt(id)
    }

    override fun size(): Int {
        return builder.npcs.size
    }

    override fun collect(): Set<NpcEntryType> {
        return builder.npcs
    }
}