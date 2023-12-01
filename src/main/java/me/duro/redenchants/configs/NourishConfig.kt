package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class NourishConfig(
    val amountPerLevel: Int = 1
)

fun nourishOptions(config: YamlConfiguration) = NourishConfig(
    amountPerLevel = config.getInt("nourish.amount-per-level", NourishConfig().amountPerLevel)
)