package me.duro.redenchants.airdrops

import org.bukkit.Location
import org.bukkit.entity.FallingBlock

object CrateList {
    val crateMap = mutableMapOf<FallingBlock, Crate>()
    val barrelList = mutableListOf<Location>()
}