package me.duro.redenchants

import me.duro.redenchants.commands.HordeCommand
import me.duro.redenchants.commands.RedEnchantsCommand
import me.duro.redenchants.commands.SoulsCommand
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.listeners.GUIListener
import me.duro.redenchants.listeners.HordeListener
import me.duro.redenchants.npcs.SoulMerchant
import me.duro.redenchants.tasks.HordeTask
import me.duro.redenchants.configs.Config
import me.duro.redenchants.utils.SoulsManager
import me.duro.redenchants.apis.SoulsPlaceholder
import me.duro.redenchants.listeners.EnchantListener
import me.duro.redenchants.npcs.Enchanter
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.trait.TraitInfo
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class RedEnchants : JavaPlugin() {
    lateinit var soulsManager: SoulsManager
    lateinit var config: Config

    override fun onEnable() {
        instance = this

        config = Config.load()
        soulsManager = SoulsManager.load()

        CustomEnchants.register()
        SoulsPlaceholder().register()

        getCommand("redenchants")?.setExecutor(RedEnchantsCommand())
        getCommand("souls")?.setExecutor(SoulsCommand())
        getCommand("horde")?.setExecutor(HordeCommand())

        server.pluginManager.registerEvents(HordeListener(), this)
        server.pluginManager.registerEvents(GUIListener(), this)
        server.pluginManager.registerEvents(EnchantListener(), this)

        try {
            val world = Bukkit.getWorld(config.data.horde.spawn.world) ?: Bukkit.getWorlds()[0]
            val center = Location(
                world,
                config.data.horde.spawn.center.first,
                config.data.horde.spawn.center.second,
                config.data.horde.spawn.center.third
            )

            HordeTask(center, config.data.horde.spawn.radius)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        CitizensAPI.getTraitFactory().apply {
            registerTrait(TraitInfo.create(SoulMerchant::class.java).withName("soul_merchant"))
            registerTrait(TraitInfo.create(Enchanter::class.java).withName("enchanter"))
        }
    }

    override fun onDisable() {
        CitizensAPI.getTraitFactory().apply {
            deregisterTrait(TraitInfo.create(SoulMerchant::class.java).withName("soul_merchant"))
            deregisterTrait(TraitInfo.create(Enchanter::class.java).withName("enchanter"))
        }

        Bukkit.getScheduler().cancelTasks(this)
    }

    companion object {
        lateinit var instance: RedEnchants
            private set
    }
}
