package com.runetopic.loader

import com.runetopic.cache.store.storage.js5.Js5Store

/**
 * @author Jordan Abraham
 */
interface IEntryProvider<T : IEntryType> {
    fun load(store: Js5Store)
    fun lookup(id: Int) : T
    fun size(): Int
    fun collect(): Set<T>
}