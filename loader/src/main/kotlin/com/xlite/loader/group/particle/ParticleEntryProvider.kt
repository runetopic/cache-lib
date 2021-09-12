package com.xlite.loader.group.particle

import com.xlite.cache.store.Store
import com.xlite.loader.IEntryProvider

/**
 * @author Jordan Abraham
 */
class ParticleEntryProvider : IEntryProvider<ParticleEntryType> {

    private val builder = ParticleEntryBuilder()

    override fun load(store: Store) {
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