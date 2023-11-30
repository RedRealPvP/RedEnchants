package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class TimberConfig(
    val amountPerLevel: Int = 5,
)

fun timberOptions(config: YamlConfiguration) = TimberConfig(
    amountPerLevel = config.getInt("timber.amount-per-level", TimberConfig().amountPerLevel)
)