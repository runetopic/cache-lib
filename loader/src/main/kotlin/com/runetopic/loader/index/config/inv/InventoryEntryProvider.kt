package com.runetopic.loader.index.config.inv

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class InventoryEntryProvider : IEntryProvider<InventoryEntryType> {

    private val builder = InventoryEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): InventoryEntryType = builder.inventoryTypes.elementAt(id)
    override fun size(): Int = builder.inventoryTypes.size
    override fun collect(): Set<InventoryEntryType> = builder.inventoryTypes
}