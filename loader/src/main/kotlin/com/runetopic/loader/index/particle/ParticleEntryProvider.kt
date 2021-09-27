package com.runetopic.loader.index.particle

import com.runetopic.cache.store.storage.js5.Js5Store
import com.runetopic.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ParticleEntryProvider : IEntryProvider<ParticleEntryType> {

    private val builder = ParticleEntryBuilder()

    override fun load(store: Js5Store) {
        builder.build(store)
    }

    override fun lookup(id: Int): ParticleEntryType {
        return builder.particles.elementAt(id)
    }

    override fun size(): Int {
        return builder.particles.size
    }

    override fun collect(): Set<ParticleEntryType> {
        return builder.particles
    }
}