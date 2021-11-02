package com.runetopic.loader.index.npc

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class NpcEntryProvider: IEntryProvider<NpcEntryType> {

    private val builder = NpcEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): NpcEntryType = builder.npcs.elementAt(id)
    override fun size(): Int = builder.npcs.size
    override fun collect(): Set<NpcEntryType> = builder.npcs
}