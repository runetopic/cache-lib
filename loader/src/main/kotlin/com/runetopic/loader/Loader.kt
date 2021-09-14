package com.runetopic.loader

import com.runetopic.loader.group.config.overlay.OverlayEntryProvider
import com.runetopic.loader.group.config.idk.IdentityKitEntryProvider
import com.runetopic.loader.group.config.inv.InventoryEntryProvider
import com.runetopic.loader.group.config.mouseicon.MouseIconEntryProvider
import com.runetopic.loader.group.config.param.ParamEntryProvider
import com.runetopic.loader.group.config.skybox.SkyBoxEntryProvider
import com.runetopic.loader.group.config.spotanim.SpotAnimationEntryProvider
import com.runetopic.loader.group.config.struct.StructEntryProvider
import com.runetopic.loader.group.config.underlay.UnderlayEntryProvider
import com.runetopic.loader.group.config.lighting.LightingEntryProvider
import com.runetopic.loader.group.loc.LocEntryProvider
import com.runetopic.loader.group.map.MapEntryProvider
import com.runetopic.loader.group.map.MapLocationEntryProvider
import com.runetopic.loader.group.npc.NpcEntryProvider
import com.runetopic.loader.group.obj.ObjEntryProvider
import com.runetopic.loader.group.particle.ParticleEntryProvider

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
fun kits(): IdentityKitEntryProvider = identityKitEntryProvider
fun skyboxes(): SkyBoxEntryProvider = skyBoxEntryProvider
fun mouseIcons(): MouseIconEntryProvider = mouseIconEntryProvider
fun invs(): InventoryEntryProvider = inventoryEntryProvider
fun lightings(): LightingEntryProvider = lightingEntryProvider
fun npcs(): NpcEntryProvider = npcEntryProvider
