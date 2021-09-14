package com.runetopic.loader

import com.runetopic.cache.store.Store

/**
 * @author Jordan Abraham
 */
internal interface IEntryProvider<T : IEntryType> {
    fun load(store: Store)
    fun lookup(id: Int) : T
    fun size(): Int
    fun collect(): Set<T>
}