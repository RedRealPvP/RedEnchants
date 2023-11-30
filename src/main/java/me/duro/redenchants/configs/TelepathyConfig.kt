package me.duro.redenchants.configs

import org.bukkit.configuration.file.YamlConfiguration

data class TelepathyConfig(
    val message: String = "&cChest placed at X: {x}, Y: {y}, Z: {z} for &4{m} minutes!",
    val chestDuration: Int = 5,
)

fun telepathyOptions(config: YamlConfiguration) = TelepathyConfig(
    message = config.getString("telepathy.message") ?: TelepathyConfig().message,
    chestDuration = config.getInt("telepathy.chest-duration", TelepathyConfig().chestDuration)
)