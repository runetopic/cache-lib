package com.xlite.loader.group.particle

import com.xlite.loader.IEntryType

/**
 * @author Jordan Abraham
 */
data class ParticleEntryType(
    private val id: Int = 0,
    var texture: Int = -1,
    var periodic: Boolean = true,
    var aBoolean3023: Boolean = false,
    var fadeColor: Int = 0,
    var localMagnets: IntArray? = null,
    var activeFirst: Boolean = true,
    var maxAngleH: Short = 0,
    var minLevel: Int = -2,
    var size1: Int = 0,
    var untextured: Int = -1,
    var maxSpeed: Int = 0,
    var maxLevel: Int = -2,
    var minSetting: Int = 0,
    var ageMark: Int = -1,
    var minLifetime: Int = 0,
    var maxLifetime: Int = 0,
    var globalMagnets: IntArray? = null,
    var minSpeed: Int = 0,
    var decelerationRate: Int = 0,
    var aBoolean3048: Boolean = true,
    var startupUpdates: Int = 0,
    var uniformColorVariance: Boolean = true,
    var size2: Int = 0,
    var decelerationType: Int = 0,
    var lifetime: Int = -1,
    var maxAngleV: Short = 0,
    var anInt3065: Int = -1,
    var maxParticleRate: Int = 0,
    var aBoolean3069: Boolean = true,
    var aBoolean3070: Boolean = true,
    var minAngleV: Short = 0,
    var minAngleH: Short = 0,
    var endSpeed: Int = -1,
    var generalMagnets: IntArray? = null,
    var aBoolean3079: Boolean = false,
    var minParticleRate: Int = 0,
    var minStartColor: Int = 0,
    var speedChangePct: Int = 100,
    var alphaFacePct: Int = 100,
    var anInt3061: Int = 100,
    var maxStartColor: Int = 0,
    var colorFadePct: Int = 100
) : IEntryType {
    override fun getId(): Int = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParticleEntryType

        if (id != other.id) return false
        if (texture != other.texture) return false
        if (periodic != other.periodic) return false
        if (aBoolean3023 != other.aBoolean3023) return false
        if (fadeColor != other.fadeColor) return false
        if (localMagnets != null) {
            if (other.localMagnets == null) return false
            if (!localMagnets.contentEquals(other.localMagnets)) return false
        } else if (other.localMagnets != null) return false
        if (activeFirst != other.activeFirst) return false
        if (maxAngleH != other.maxAngleH) return false
        if (minLevel != other.minLevel) return false
        if (size1 != other.size1) return false
        if (untextured != other.untextured) return false
        if (maxSpeed != other.maxSpeed) return false
        if (maxLevel != other.maxLevel) return false
        if (minSetting != other.minSetting) return false
        if (ageMark != other.ageMark) return false
        if (minLifetime != other.minLifetime) return false
        if (maxLifetime != other.maxLifetime) return false
        if (globalMagnets != null) {
            if (other.globalMagnets == null) return false
            if (!globalMagnets.contentEquals(other.globalMagnets)) return false
        } else if (other.globalMagnets != null) return false
        if (minSpeed != other.minSpeed) return false
        if (decelerationRate != other.decelerationRate) return false
        if (aBoolean3048 != other.aBoolean3048) return false
        if (startupUpdates != other.startupUpdates) return false
        if (uniformColorVariance != other.uniformColorVariance) return false
        if (size2 != other.size2) return false
        if (decelerationType != other.decelerationType) return false
        if (lifetime != other.lifetime) return false
        if (maxAngleV != other.maxAngleV) return false
        if (anInt3065 != other.anInt3065) return false
        if (maxParticleRate != other.maxParticleRate) return false
        if (aBoolean3069 != other.aBoolean3069) return false
        if (aBoolean3070 != other.aBoolean3070) return false
        if (minAngleV != other.minAngleV) return false
        if (minAngleH != other.minAngleH) return false
        if (endSpeed != other.endSpeed) return false
        if (generalMagnets != null) {
            if (other.generalMagnets == null) return false
            if (!generalMagnets.contentEquals(other.generalMagnets)) return false
        } else if (other.generalMagnets != null) return false
        if (aBoolean3079 != other.aBoolean3079) return false
        if (minParticleRate != other.minParticleRate) return false
        if (minStartColor != other.minStartColor) return false
        if (speedChangePct != other.speedChangePct) return false
        if (alphaFacePct != other.alphaFacePct) return false
        if (anInt3061 != other.anInt3061) return false
        if (maxStartColor != other.maxStartColor) return false
        if (colorFadePct != other.colorFadePct) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + texture
        result = 31 * result + periodic.hashCode()
        result = 31 * result + aBoolean3023.hashCode()
        result = 31 * result + fadeColor
        result = 31 * result + (localMagnets?.contentHashCode() ?: 0)
        result = 31 * result + activeFirst.hashCode()
        result = 31 * result + maxAngleH
        result = 31 * result + minLevel
        result = 31 * result + size1
        result = 31 * result + untextured
        result = 31 * result + maxSpeed
        result = 31 * result + maxLevel
        result = 31 * result + minSetting
        result = 31 * result + ageMark
        result = 31 * result + minLifetime
        result = 31 * result + maxLifetime
        result = 31 * result + (globalMagnets?.contentHashCode() ?: 0)
        result = 31 * result + minSpeed
        result = 31 * result + decelerationRate
        result = 31 * result + aBoolean3048.hashCode()
        result = 31 * result + startupUpdates
        result = 31 * result + uniformColorVariance.hashCode()
        result = 31 * result + size2
        result = 31 * result + decelerationType
        result = 31 * result + lifetime
        result = 31 * result + maxAngleV
        result = 31 * result + anInt3065
        result = 31 * result + maxParticleRate
        result = 31 * result + aBoolean3069.hashCode()
        result = 31 * result + aBoolean3070.hashCode()
        result = 31 * result + minAngleV
        result = 31 * result + minAngleH
        result = 31 * result + endSpeed
        result = 31 * result + (generalMagnets?.contentHashCode() ?: 0)
        result = 31 * result + aBoolean3079.hashCode()
        result = 31 * result + minParticleRate
        result = 31 * result + minStartColor
        result = 31 * result + speedChangePct
        result = 31 * result + alphaFacePct
        result = 31 * result + anInt3061
        result = 31 * result + maxStartColor
        result = 31 * result + colorFadePct
        return result
    }
}