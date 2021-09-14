package com.runetopic.loader.group.map

import com.runetopic.loader.IEntryType

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 */
data class MapLocationEntryType(
    private val regionId: Int,
    private val regionX: Int,
    private val regionY: Int,
    val locations: ArrayList<MapLocation> = arrayListOf()
): IEntryType {
    override fun getId(): Int = regionId

    class MapLocation(
        private val id: Int,
        private val type: Int,
        private val orientation: Int,
        private val localX: Int,
        private val localY: Int,
        private val height: Int
    )
}