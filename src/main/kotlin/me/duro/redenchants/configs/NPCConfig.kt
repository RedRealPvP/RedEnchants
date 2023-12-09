package me.duro.redenchants.configs

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration

data class NPCConfig(
    val enchanterLocation: Location = Location(Bukkit.getWorlds()[0], -200.0, 203.0, 423.0),
    val soulMerchantLocation: Location = Location(Bukkit.getWorlds()[0], -195.0, 203.0, 423.0),
    val headHunterLocation: Location = Location(Bukkit.getWorlds()[0], -190.0, 203.0, 423.0),
)

fun npcOptions(config: YamlConfiguration) = NPCConfig(
    enchanterLocation = Location(
        Bukkit.getWorld(config.getString("enchanter.world") ?: Bukkit.getWorlds()[0].name),
        config.getDouble("enchanter.x", NPCConfig().enchanterLocation.x),
        config.getDouble("enchanter.y", NPCConfig().enchanterLocation.y),
        config.getDouble("enchanter.z", NPCConfig().enchanterLocation.z),
    ),
    soulMerchantLocation = Location(
        Bukkit.getWorld(config.getString("soul-merchant.world") ?: Bukkit.getWorlds()[0].name),
        config.getDouble("soul-merchant.x", NPCConfig().soulMerchantLocation.x),
        config.getDouble("soul-merchant.y", NPCConfig().soulMerchantLocation.y),
        config.getDouble("soul-merchant.z", NPCConfig().soulMerchantLocation.z),
    ),
    headHunterLocation = Location(
        Bukkit.getWorld(config.getString("head-hunter.world") ?: Bukkit.getWorlds()[0].name),
        config.getDouble("head-hunter.x", NPCConfig().headHunterLocation.x),
        config.getDouble("head-hunter.y", NPCConfig().headHunterLocation.y),
        config.getDouble("head-hunter.z", NPCConfig().headHunterLocation.z),
    ),
)