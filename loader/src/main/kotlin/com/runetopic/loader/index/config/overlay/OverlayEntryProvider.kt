package com.runetopic.loader.index.config.overlay

import com.runetopic.cache.store.storage.js5.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class OverlayEntryProvider: IEntryProvider<OverlayEntryType> {

    private val builder = OverlayEntryBuilder()

    override fun load(store: Js5Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): OverlayEntryType {
        return builder.overlays.elementAt(id)
    }

    override fun size(): Int {
        return builder.overlays.size
    }

    override fun collect(): Set<OverlayEntryType> {
        return builder.overlays
    }
}