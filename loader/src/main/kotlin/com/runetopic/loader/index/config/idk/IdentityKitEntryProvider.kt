package com.runetopic.loader.index.config.idk

import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class IdentityKitEntryProvider : IEntryProvider<IdentityKitEntryType> {

    private val builder = IdentityKitEntryBuilder()

    override fun load(store: Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): IdentityKitEntryType {
        return builder.identityKitTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.identityKitTypes.size
    }

    override fun collect(): Set<IdentityKitEntryType> {
        return builder.identityKitTypes
    }
}