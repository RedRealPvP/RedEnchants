package me.duro.redenchants.configs

import me.duro.redenchants.RedEnchants
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class ConfigFile(
    val horde: HordeConfig = HordeConfig(),
    val telepathy: TelepathyConfig = TelepathyConfig(),
    val shotgun: ShotgunConfig = ShotgunConfig(),
    val timber: TimberConfig = TimberConfig(),
    val vampiric: VampiricConfig = VampiricConfig(),
)

class Config {
    lateinit var data: ConfigFile

    fun load(): Config {
        val file = File(RedEnchants.instance.dataFolder, "config.yml")

        if (!file.exists()) RedEnchants.instance.saveResource("config.yml", false)

        val configFile = YamlConfiguration().apply { options().parseComments(true) }

        runCatching { configFile.load(file) }.onFailure { it.printStackTrace() }

        data = ConfigFile(
            horde = hordeOptions(configFile),
            telepathy = telepathyOptions(configFile),
            shotgun = shotgunOptions(configFile),
            timber = timberOptions(configFile),
            vampiric = vampiricOptions(configFile),
        )

        return this
    }

    companion object {
        fun load() = Config().load()
    }
}