package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class TracerConfig(
    val chancePerLevel: Double = 0.1,
)

fun tracerOptions(config: YamlConfiguration) = TracerConfig(
    chancePerLevel = config.getDouble("tracer.chance-per-level", TracerConfig().chancePerLevel),
)
