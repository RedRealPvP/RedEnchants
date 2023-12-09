package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class DecapitatorConfig(
    val balancePercent: Double = 0.05,
)

fun decapitatorOptions(config: YamlConfiguration) = DecapitatorConfig(
    balancePercent = config.getDouble("decapitator.balance-percent", 0.05)
)