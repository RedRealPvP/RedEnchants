package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class HordeConfig(
    val messages: HordeMessageOptions = HordeMessageOptions(),
    val spawn: HordeSpawnOptions = HordeSpawnOptions(),
    val interval: Int = 120,
    val duration: Int = 60,
)

data class HordeSpawnOptions(
    val amount: Pair<Int, Int> = Pair(2, 4),
    val world: String = "world",
    val center: Triple<Double, Double, Double> = Triple(-186.0, 88.0, 495.0),
    val radius: Double = 150.0,
    val groups: Int = 25,
)

data class HordeMessageOptions(
    val spawn: String = "&c&lA horde has spawned in the warzone!",
    val kill: String = "&cYou have slain a horde member and gained {souls} soul!\nYou now have {total} souls.",
)

fun hordeOptions(config: YamlConfiguration) = HordeConfig(
    messages = HordeMessageOptions(
        config.getString("horde.messages.spawn", HordeConfig().messages.spawn) ?: HordeConfig().messages.spawn,
        config.getString("horde.messages.kill", HordeConfig().messages.kill) ?: HordeConfig().messages.kill,
    ),
    spawn = HordeSpawnOptions(
        Pair(
            config.getInt("horde.spawn.amount.min", HordeConfig().spawn.amount.first),
            config.getInt("horde.spawn.amount.max", HordeConfig().spawn.amount.second),
        ),
        config.getString("horde.spawn.world", HordeConfig().spawn.world) ?: HordeConfig().spawn.world,
        Triple(
            config.getDouble("horde.spawn.center.x", HordeConfig().spawn.center.first),
            config.getDouble("horde.spawn.center.y", HordeConfig().spawn.center.second),
            config.getDouble("horde.spawn.center.z", HordeConfig().spawn.center.third),
        ),
        config.getDouble("horde.spawn.radius", HordeConfig().spawn.radius),
        config.getInt("horde.spawn.groups", HordeConfig().spawn.groups),
    ),
    interval = config.getInt("horde.interval", HordeConfig().interval),
    duration = config.getInt("horde.duration", HordeConfig().duration),
)