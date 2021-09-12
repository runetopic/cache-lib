package com.xlite.loader.group.config.overlay

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class OverlayEntryProvider: IEntryProvider<OverlayEntryType> {

    private val builder = OverlayEntryBuilder()

    override fun load(store: Store) {
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