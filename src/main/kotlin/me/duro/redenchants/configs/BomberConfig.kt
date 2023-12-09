package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class BomberConfig(
    val chancePerLevel: Double = 0.1,
    val forcePerLevel: Double = 0.25,
)

fun bomberOptions(config: YamlConfiguration) = BomberConfig(
    chancePerLevel = config.getDouble("bomber.chance-per-level", BomberConfig().chancePerLevel),
    forcePerLevel = config.getDouble("bomber.force-per-level", BomberConfig().forcePerLevel),
)
