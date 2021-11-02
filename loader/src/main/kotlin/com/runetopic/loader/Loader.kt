package com.runetopic.loader

import com.runetopic.loader.index.config.idk.IdentityKitEntryProvider
import com.runetopic.loader.index.config.inv.InventoryEntryProvider
import com.runetopic.loader.index.config.lighting.LightingEntryProvider
import com.runetopic.loader.index.config.mouseicon.MouseIconEntryProvider
import com.runetopic.loader.index.config.overlay.OverlayEntryProvider
import com.runetopic.loader.index.config.param.ParamEntryProvider
import com.runetopic.loader.index.config.skybox.SkyBoxEntryProvider
import com.runetopic.loader.index.config.struct.StructEntryProvider
import com.runetopic.loader.index.config.underlay.UnderlayEntryProvider
import com.runetopic.loader.index.loc.LocEntryProvider
import com.runetopic.loader.index.map.MapEntryProvider
import com.runetopic.loader.index.map.MapLocationEntryProvider
import com.runetopic.loader.index.npc.NpcEntryProvider
import com.runetopic.loader.index.obj.ObjEntryProvider
import com.runetopic.loader.index.particle.ParticleEntryProvider
import com.runetopic.loader.index.spotanim.SpotAnimationEntryProvider

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
private val skyBoxEntryProvider = SkyBoxEntryProvider()
private val mouseIconEntryProvider = MouseIconEntryProvider()
private val inventoryEntryProvider = InventoryEntryProvider()
private val lightingEntryProvider = LightingEntryProvider()
private val identityKitProvider = IdentityKitEntryProvider()
private val npcEntryProvider = NpcEntryProvider()

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
fun kits(): IdentityKitEntryProvider = identityKitProvider
fun skyboxes(): SkyBoxEntryProvider = skyBoxEntryProvider
fun mouseIcons(): MouseIconEntryProvider = mouseIconEntryProvider
fun invs(): InventoryEntryProvider = inventoryEntryProvider
fun lightings(): LightingEntryProvider = lightingEntryProvider
fun npcs(): NpcEntryProvider = npcEntryProvider
