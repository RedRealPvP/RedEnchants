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
    cooldown = config.getInt("shotgun.cooldown", ShotgunConfig().cooldown),
    initialAngle = config.getDouble("shotgun.initial-angle", ShotgunConfig().initialAngle),
    anglePerLevel = config.getDouble("shotgun.angle-per-level", ShotgunConfig().anglePerLevel),
    initialArrows = config.getInt("shotgun.initial-arrows", ShotgunConfig().initialArrows),
    arrowsPerLevel = config.getInt("shotgun.arrows-per-level", ShotgunConfig().arrowsPerLevel),
    damageMultiplier = config.getDouble("shotgun.damage-multiplier", ShotgunConfig().damageMultiplier),
)

