package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class SpikedHookConfig(
    val damagePerLevel: Double = 1.0,
)

fun spikedHookOptions(config: YamlConfiguration) = SpikedHookConfig(
    damagePerLevel = config.getDouble("damage-per-level", SpikedHookConfig().damagePerLevel),
)
