package com.xlite.loader

import com.xlite.loader.group.config.spotanim.SpotAnimationEntryProvider
import com.xlite.loader.group.config.struct.StructEntryProvider
import com.xlite.loader.group.map.MapEntryProvider
import com.xlite.loader.group.obj.ObjEntryProvider

/**
 * @author Jordan Abraham
 */
private val spotAnimationEntryProvider = SpotAnimationEntryProvider()
private val structEntryProvider = StructEntryProvider()
private val mapEntryProvider = MapEntryProvider()
private val objProvider = ObjEntryProvider()

fun spotAnimations(): SpotAnimationEntryProvider = spotAnimationEntryProvider
fun structs(): StructEntryProvider = structEntryProvider
fun maps(): MapEntryProvider = mapEntryProvider
fun objs(): ObjEntryProvider = objProvider