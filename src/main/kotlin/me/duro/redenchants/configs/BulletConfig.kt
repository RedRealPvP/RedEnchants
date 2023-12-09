package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class BulletConfig(
    val speedPerLevel: Double = 0.1,
)

fun bulletOptions(config: YamlConfiguration) = BulletConfig(
    speedPerLevel = config.getDouble("bullet.speed-per-level", BulletConfig().speedPerLevel),
)
