package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class VampiricConfig(
    val healthPerLevel: Double = 0.25,
    val chancePerLevel: Double = 0.05,
)

fun vampiricOptions(config: YamlConfiguration) = VampiricConfig(
    healthPerLevel = config.getDouble("vampiric.health-per-level", VampiricConfig().healthPerLevel),
    chancePerLevel = config.getDouble("vampiric.chance-per-level", VampiricConfig().chancePerLevel),
)
