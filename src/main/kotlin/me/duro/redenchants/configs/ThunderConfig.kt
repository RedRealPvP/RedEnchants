package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class ThunderConfig(
    val chancePerLevel: Double = 0.05,
)

fun thunderOptions(config: YamlConfiguration) = ThunderConfig(
    chancePerLevel = config.getDouble("thunder.chance-per-level", ThunderConfig().chancePerLevel),
)
