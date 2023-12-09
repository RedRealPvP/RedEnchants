package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class CleaveConfig(
    val radiusPerLevel: Int = 1,
    val chancePerLevel: Double = 0.05,
)

fun cleaveOptions(config: YamlConfiguration) = CleaveConfig(
    radiusPerLevel = config.getInt("cleave.radius-per-level", CleaveConfig().radiusPerLevel),
    chancePerLevel = config.getDouble("cleave.chance-per-level", CleaveConfig().chancePerLevel),
)
