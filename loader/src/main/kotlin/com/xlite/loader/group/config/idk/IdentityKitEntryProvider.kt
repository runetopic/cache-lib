package com.xlite.loader.group.config.idk

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider


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