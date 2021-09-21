package com.runetopic.loader.index.config.inv

import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class InventoryEntryProvider : IEntryProvider<InventoryEntryType> {

    private val builder = InventoryEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): InventoryEntryType {
        return builder.inventoryTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.inventoryTypes.size
    }

    override fun collect(): Set<InventoryEntryType> {
        return builder.inventoryTypes
    }
}