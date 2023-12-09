package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class TelepathyConfig(
    val message: String = "&7Chest placed at &c{x}&7, &c{y}&7, &c{z}&7 for &c{m}&7 minutes!",
    val chestDuration: Int = 5,
)

fun telepathyOptions(config: YamlConfiguration) = TelepathyConfig(
    message = config.getString("telepathy.message") ?: TelepathyConfig().message,
    chestDuration = config.getInt("telepathy.chest-duration", TelepathyConfig().chestDuration)
)