package com.runetopic.loader.group.particle

import com.runetopic.cache.extension.readUnsignedByte
import com.runetopic.cache.extension.readUnsignedShort
import com.runetopic.cache.extension.skip
import com.runetopic.cache.extension.toBoolean
import com.runetopic.cache.store.Store
import com.runetopic.loader.IEntryBuilder
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
internal class ParticleEntryBuilder : IEntryBuilder<ParticleEntryType> {

    lateinit var particles: Set<ParticleEntryType>

    @OptIn(ExperimentalStdlibApi::class)
    override fun build(store: Store) {
        particles = buildSet {
            store.index(27).use { group ->
                group.entries(0).forEach {
                    add(read(ByteBuffer.wrap(store.file(group, it.groupId, it.id).data), ParticleEntryType(it.id)))
                }
            }
        }
    }

    override fun read(buffer: ByteBuffer, type: ParticleEntryType): ParticleEntryType {
        do when (val opcode: Int = buffer.readUnsignedByte()) {
            0 -> break
            1 -> {
                type.minAngleH = (buffer.readUnsignedShort() shl 3).toShort()
                type.maxAngleH = (buffer.readUnsignedShort() shl 3).toShort()
                type.minAngleV = (buffer.readUnsignedShort() shl 3).toShort()
                type.maxAngleV = (buffer.readUnsignedShort() shl 3).toShort()
            }
            2 -> buffer.skip(1)
            3 -> {
                type.minSpeed = buffer.int
                type.maxSpeed = buffer.int
            }
            4 -> {
                type.decelerationType = buffer.readUnsignedByte()
                type.decelerationRate = buffer.get().toInt()
            }
            5 -> type.size1 = buffer.readUnsignedShort() shl 12 shl 2.also { type.size2 = it }
            6 -> {
                type.minStartColor = buffer.int
                type.maxStartColor = buffer.int
            }
            7 -> {
                type.minLifetime = buffer.readUnsignedShort()
                type.maxLifetime = buffer.readUnsignedShort()
            }
            8 -> {
                type.minParticleRate = buffer.readUnsignedShort()
                type.maxParticleRate = buffer.readUnsignedShort()
            }
            9 -> {
                val size = buffer.readUnsignedByte()
                val localMagnets = IntArray(size)
                (0 until size).forEach { localMagnets[it] = buffer.readUnsignedShort() }
                type.localMagnets = localMagnets
            }
            10 -> {
                val size = buffer.readUnsignedByte()
                val globalMagnets = IntArray(size)
                (0 until size).forEach { globalMagnets[it] = buffer.readUnsignedShort() }
                type.globalMagnets = globalMagnets
            }
            12 -> type.minLevel = buffer.get().toInt()
            13 -> type.maxLevel = buffer.get().toInt()
            14 -> type.startupUpdates = buffer.readUnsignedShort()
            15 -> type.texture = buffer.readUnsignedShort()
            16 -> {
                type.activeFirst = buffer.readUnsignedByte().toBoolean()
                type.ageMark = buffer.readUnsignedShort()
                type.lifetime = buffer.readUnsignedShort()
                type.periodic = buffer.readUnsignedByte().toBoolean()
            }
            17 -> type.untextured = buffer.readUnsignedShort()
            18 -> type.fadeColor = buffer.int
            19 -> type.minSetting = buffer.readUnsignedByte()
            20 -> type.colorFadePct = buffer.readUnsignedByte()
            21 -> type.alphaFacePct = buffer.readUnsignedByte()
            22 -> type.endSpeed = buffer.int
            23 -> type.speedChangePct = buffer.readUnsignedByte()
            24 -> type.uniformColorVariance = false
            25 -> {
                val size = buffer.readUnsignedByte()
                val generalMagnets = IntArray(size)
                (0 until size).forEach { generalMagnets[it] = buffer.readUnsignedShort() }
                type.generalMagnets = generalMagnets
            }
            26 -> type.aBoolean3070 = false
            27 -> type.anInt3065 = buffer.readUnsignedShort() shl 12 shl 2
            28 -> type.anInt3061 = buffer.readUnsignedByte()
            29 -> buffer.skip(2)
            30 -> type.aBoolean3079 = true
            31 -> {
                type.size1 = buffer.readUnsignedShort() shl 12 shl 2
                type.size2 = buffer.readUnsignedShort() shl 12 shl 2
            }
            32 -> type.aBoolean3048 = false
            33 -> type.aBoolean3023 = true
            34 -> type.aBoolean3069 = false
            else -> throw Exception("Read unused opcode with id: ${opcode}.")
        } while (true)
        return type
    }
}