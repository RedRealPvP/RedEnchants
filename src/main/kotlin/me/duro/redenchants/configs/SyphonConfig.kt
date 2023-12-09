package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class SyphonConfig(
    val healthPerLevel: Double = 0.25,
    val chancePerLevel: Double = 0.05,
)

fun syphonOptions(config: YamlConfiguration) = SyphonConfig(
    healthPerLevel = config.getDouble("syphon.health-per-level", SyphonConfig().healthPerLevel),
    chancePerLevel = config.getDouble("syphon.chance-per-level", SyphonConfig().chancePerLevel),
)
