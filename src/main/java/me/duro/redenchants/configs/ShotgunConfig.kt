package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class ShotgunConfig(
    val cooldown: Int = 1,
    val initialAngle: Double = 10.0,
    val anglePerLevel: Double = 5.0,
    val initialArrows: Int = 5,
    val arrowsPerLevel: Int = 0,
    val damageMultiplier: Double = 2.0,
)

fun shotgunOptions(config: YamlConfiguration) = ShotgunConfig(
    cooldown = config.getInt("cooldown", ShotgunConfig().cooldown),
    initialAngle = config.getDouble("initial-angle", ShotgunConfig().initialAngle),
    anglePerLevel = config.getDouble("angle-per-level", ShotgunConfig().anglePerLevel),
    initialArrows = config.getInt("initial-arrows", ShotgunConfig().initialArrows),
    arrowsPerLevel = config.getInt("arrows-per-level", ShotgunConfig().arrowsPerLevel),
    damageMultiplier = config.getDouble("damage-multiplier", ShotgunConfig().damageMultiplier),
)

