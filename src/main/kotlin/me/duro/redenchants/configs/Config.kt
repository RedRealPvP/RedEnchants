package me.duro.redenchants.configs

import me.duro.redenchants.RedEnchants
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class ConfigFile(
    val horde: HordeConfig = HordeConfig(),
    val telepathy: TelepathyConfig = TelepathyConfig(),
    val shotgun: ShotgunConfig = ShotgunConfig(),
    val timber: TimberConfig = TimberConfig(),
    val syphon: SyphonConfig = SyphonConfig(),
    val nourish: NourishConfig = NourishConfig(),
    val npcs: NPCConfig = NPCConfig(),
    val decapitator: DecapitatorConfig = DecapitatorConfig(),
    val doubleCatch: DoubleCatchConfig = DoubleCatchConfig(),
    val flashbang: FlashbangConfig = FlashbangConfig(),
    val cleave: CleaveConfig = CleaveConfig(),
    val frostbiteConfig: FrostbiteConfig = FrostbiteConfig(),
    val thunder: ThunderConfig = ThunderConfig(),
    val scavenger: ScavengerConfig = ScavengerConfig(),
    val bullet: BulletConfig = BulletConfig(),
    val bomber: BomberConfig = BomberConfig(),
    val tracer: TracerConfig = TracerConfig(),
    val experience: ExperienceConfig = ExperienceConfig(),
    val spikedHook: SpikedHookConfig = SpikedHookConfig(),
)

class Config {
    private lateinit var configFile: YamlConfiguration
    private val file = File(RedEnchants.instance.dataFolder, "config.yml")
    lateinit var data: ConfigFile

    fun load(): Config {
        if (!file.exists()) RedEnchants.instance.saveResource("config.yml", false)

        configFile = YamlConfiguration().apply { options().parseComments(true) }

        runCatching { configFile.load(file) }.onFailure { it.printStackTrace() }

        data = ConfigFile(
            horde = hordeOptions(configFile),
            telepathy = telepathyOptions(configFile),
            shotgun = shotgunOptions(configFile),
            timber = timberOptions(configFile),
            syphon = syphonOptions(configFile),
            nourish = nourishOptions(configFile),
            npcs = npcOptions(configFile),
            decapitator = decapitatorOptions(configFile),
            doubleCatch = doubleCatchOptions(configFile),
            flashbang = flashbangOptions(configFile),
            cleave = cleaveOptions(configFile),
            frostbiteConfig = frostbiteOptions(configFile),
            thunder = thunderOptions(configFile),
            scavenger = scavengerOptions(configFile),
            bullet = bulletOptions(configFile),
            bomber = bomberOptions(configFile),
            tracer = tracerOptions(configFile),
            experience = experienceOptions(configFile),
            spikedHook = spikedHookOptions(configFile),
        )

        return this
    }

    companion object {
        fun load() = Config().load()
    }
}