package me.duro.redenchants.configs

data class FrostbiteConfig(
    val chancePerLevel: Double = 0.05,
    val durationPerLevel: Int = 5,
)

fun frostbiteOptions(config: org.bukkit.configuration.file.YamlConfiguration) = FrostbiteConfig(
    chancePerLevel = config.getDouble("frostbite.chance-per-level", FrostbiteConfig().chancePerLevel),
    durationPerLevel = config.getInt("frostbite.duration-per-level", FrostbiteConfig().durationPerLevel),
)
