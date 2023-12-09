package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class DoubleCatchConfig(
    val chancePerLevel: Double = 0.02,
)

fun doubleCatchOptions(config: YamlConfiguration) = DoubleCatchConfig(
    chancePerLevel = config.getDouble("double-catch.chance-per-level", DoubleCatchConfig().chancePerLevel),
)