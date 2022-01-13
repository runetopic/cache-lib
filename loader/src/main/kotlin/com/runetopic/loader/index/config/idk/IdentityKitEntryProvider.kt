package com.runetopic.loader.index.config.idk

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class IdentityKitEntryProvider : IEntryProvider<IdentityKitEntryType> {

    private val builder = IdentityKitEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): IdentityKitEntryType = builder.identityKitTypes.elementAt(id)
    override fun size(): Int = builder.identityKitTypes.size
    override fun collect(): Set<IdentityKitEntryType> = builder.identityKitTypes
}
