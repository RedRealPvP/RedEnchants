package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class FlashbangConfig(
    val chancePerLevel: Double = 0.05,
    val durationPerLevel: Int = 2,
)

fun flashbangOptions(config: YamlConfiguration) = FlashbangConfig(
    chancePerLevel = config.getDouble("flashbang.chance-per-level", FlashbangConfig().chancePerLevel),
    durationPerLevel = config.getInt("flashbang.duration-per-level", FlashbangConfig().durationPerLevel),
)
