package me.duro.redenchants.utils

import me.duro.redenchants.RedEnchants
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class TelepathyOptions(
    val message: String = "&cChest placed at X: {x}, Y: {y}, Z: {z} for &4{m} minutes!",
    val chestDuration: Int = 5,
)

data class TimberOptions(
    val amountPerLevel: Int = 5,
)

data class ShotgunOptions(
    val cooldown: Int = 1,
    val initialAngle: Double = 10.0,
    val anglePerLevel: Double = 5.0,
    val damageMultiplier: Double = 2.0,
)

data class Decapitator(
    val dropChance: Double = 0.1,
)

data class ConfigFile(
    val telepathy: TelepathyOptions = TelepathyOptions(),
    val timber: TimberOptions = TimberOptions(),
    val shotgun: ShotgunOptions = ShotgunOptions(),
    val decapitator: Decapitator = Decapitator(),
)

class Config {
    lateinit var data: ConfigFile

    fun load(): Config {
        val file = File(RedEnchants.instance.dataFolder, "config.yml")

        if (!file.exists()) RedEnchants.instance.saveResource("config.yml", false)

        val configFile = YamlConfiguration()
        configFile.options().parseComments(true)

        try {
            configFile.load(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        data = ConfigFile(
            TelepathyOptions(
                configFile.getString("telepathy.message") ?: TelepathyOptions().message,
                configFile.getInt("telepathy.chest-duration", TelepathyOptions().chestDuration)
            ),
            TimberOptions(
                configFile.getInt("timber.amount-per-level", TimberOptions().amountPerLevel)
            ),
            ShotgunOptions(
                configFile.getInt("shotgun.cooldown", ShotgunOptions().cooldown),
                configFile.getDouble("shotgun.initial-angle", ShotgunOptions().initialAngle),
                configFile.getDouble("shotgun.angle-per-level", ShotgunOptions().anglePerLevel),
                configFile.getDouble("shotgun.damage-multiplier", ShotgunOptions().damageMultiplier),
            ),
            Decapitator(
                configFile.getDouble("decapitator.drop-chance", Decapitator().dropChance)
            )
        )

        return this
    }

    companion object {
        fun load() = Config().load()
    }
}