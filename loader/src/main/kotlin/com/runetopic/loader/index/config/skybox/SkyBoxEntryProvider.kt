package com.runetopic.loader.index.config.skybox

import com.runetopic.cache.store.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class SkyBoxEntryProvider : IEntryProvider<SkyBoxEntryType> {

    private val builder = SkyBoxEntryBuilder()

    override fun load(store: Js5Store) = builder.build(store)
    override fun lookup(id: Int): SkyBoxEntryType = builder.skyBoxTypes.elementAt(id)
    override fun size(): Int = builder.skyBoxTypes.size
    override fun collect(): Set<SkyBoxEntryType> = builder.skyBoxTypes
}
