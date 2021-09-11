package com.xlite.loader

import com.xlite.loader.group.config.spotanim.SpotAnimationEntryProvider
import com.xlite.loader.group.config.struct.StructEntryProvider
import com.xlite.loader.group.config.underlay.UnderlayEntryProvider
import com.xlite.loader.group.map.MapEntryProvider
import com.xlite.loader.group.map.MapLocationEntryProvider
import com.xlite.loader.group.obj.ObjEntryProvider
import com.xlite.loader.group.particle.ParticleEntryProvider

/**
 * @author Jordan Abraham
 */
private val spotAnimationEntryProvider = SpotAnimationEntryProvider()
private val structEntryProvider = StructEntryProvider()
private val mapEntryProvider = MapEntryProvider()
private val objEntryProvider = ObjEntryProvider()
private val mapLocationEntryProvider = MapLocationEntryProvider()
private val particlesEntryProvider = ParticleEntryProvider()
private val underlaysEntryProvider = UnderlayEntryProvider()

fun spotAnimations(): SpotAnimationEntryProvider = spotAnimationEntryProvider
fun structs(): StructEntryProvider = structEntryProvider
fun maps(): MapEntryProvider = mapEntryProvider
fun objs(): ObjEntryProvider = objEntryProvider
fun mapLocs(): MapLocationEntryProvider = mapLocationEntryProvider
fun particles(): ParticleEntryProvider = particlesEntryProvider
fun underlays(): UnderlayEntryProvider = underlaysEntryProvider