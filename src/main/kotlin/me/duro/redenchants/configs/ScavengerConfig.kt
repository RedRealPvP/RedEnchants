package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class ScavengerConfig(
    val chancePerLevel: Double = 0.05,
    val amountPerLevel: Double = 1.0,
)

fun scavengerOptions(config: YamlConfiguration) = ScavengerConfig(
    chancePerLevel = config.getDouble("scavenger.chance-per-level", ScavengerConfig().chancePerLevel),
    amountPerLevel = config.getDouble("scavenger.amount-per-level", ScavengerConfig().amountPerLevel),
)