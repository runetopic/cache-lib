package com.runetopic.loader.index.config.skybox

import com.runetopic.cache.store.storage.js5.Js5Store
import com.runetopic.loader.IEntryProvider


/**
 * @author Jordan Abraham
 */
class SkyBoxEntryProvider : IEntryProvider<SkyBoxEntryType> {

    private val builder = SkyBoxEntryBuilder()

    override fun load(store: Js5Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): SkyBoxEntryType {
        return builder.skyBoxTypes.elementAt(id)
    }

    override fun size(): Int {
        return builder.skyBoxTypes.size
    }

    override fun collect(): Set<SkyBoxEntryType> {
        return builder.skyBoxTypes
    }
}