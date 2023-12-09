package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class ExperienceConfig(
    val chancePerLevel: Double = 0.1,
    val multiplierPerLevel: Double = 0.25,
)

fun experienceOptions(config: YamlConfiguration) = ExperienceConfig(
    chancePerLevel = config.getDouble("experience.chance-per-level", ExperienceConfig().chancePerLevel),
    multiplierPerLevel = config.getDouble("bomber.multiplier-per-level", ExperienceConfig().multiplierPerLevel),
)
