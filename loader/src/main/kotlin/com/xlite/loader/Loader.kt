package com.xlite.loader

import com.xlite.loader.group.config.overlay.OverlayEntryProvider
import com.xlite.loader.group.config.idk.IdentityKitEntryProvider
import com.xlite.loader.group.config.inv.InventoryEntryProvider
import com.xlite.loader.group.config.mouseicon.MouseIconEntryProvider
import com.xlite.loader.group.config.param.ParamEntryProvider
import com.xlite.loader.group.config.skybox.SkyBoxEntryProvider
import com.xlite.loader.group.config.spotanim.SpotAnimationEntryProvider
import com.xlite.loader.group.config.struct.StructEntryProvider
import com.xlite.loader.group.config.underlay.UnderlayEntryProvider
import com.xlite.loader.group.lighting.AtmosphereLightingEntryProvider
import com.xlite.loader.group.loc.LocEntryProvider
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
private val locEntryProvider = LocEntryProvider()
private val paramEntryProvider = ParamEntryProvider()
private val overlayEntryProvider = OverlayEntryProvider()
private val identityKitEntryProvider = IdentityKitEntryProvider()
private val skyBoxEntryProvider = SkyBoxEntryProvider()
private val mouseIconEntryProvider = MouseIconEntryProvider()
private val inventoryEntryProvider = InventoryEntryProvider()
private val atmosphereLightingEntryProvider = AtmosphereLightingEntryProvider()

fun spotAnimations(): SpotAnimationEntryProvider = spotAnimationEntryProvider
fun structs(): StructEntryProvider = structEntryProvider
fun maps(): MapEntryProvider = mapEntryProvider
fun objs(): ObjEntryProvider = objEntryProvider
fun mapLocs(): MapLocationEntryProvider = mapLocationEntryProvider
fun particles(): ParticleEntryProvider = particlesEntryProvider
fun underlays(): UnderlayEntryProvider = underlaysEntryProvider
fun locs(): LocEntryProvider = locEntryProvider
fun params(): ParamEntryProvider = paramEntryProvider
fun overlays(): OverlayEntryProvider = overlayEntryProvider
fun kits(): IdentityKitEntryProvider = identityKitEntryProvider
fun skyboxes(): SkyBoxEntryProvider = skyBoxEntryProvider
fun mouseIcons(): MouseIconEntryProvider = mouseIconEntryProvider
fun invs(): InventoryEntryProvider = inventoryEntryProvider
fun atmosphereLightings(): AtmosphereLightingEntryProvider = atmosphereLightingEntryProvider
