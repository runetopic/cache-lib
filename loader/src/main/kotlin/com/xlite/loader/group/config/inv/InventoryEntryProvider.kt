package com.xlite.loader.group.config.inv

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


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