package me.duro.redenchants

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.duro.redenchants.airdrops.CrateStorage
import me.duro.redenchants.commands.HordeCommand
import me.duro.redenchants.commands.RedEnchantsCommand
import me.duro.redenchants.commands.SoulsCommand
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.listeners.GUIListener
import me.duro.redenchants.listeners.HordeListener
import me.duro.redenchants.tasks.HordeTask
import me.duro.redenchants.configs.Config
import me.duro.redenchants.utils.SoulsManager
import me.duro.redenchants.apis.SoulsPlaceholder
import me.duro.redenchants.apis.VaultHook
import me.duro.redenchants.listeners.EnchantListener
import me.duro.redenchants.listeners.AirdropListener
import me.duro.redenchants.npcs.NPCRegistry
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class RedEnchants : JavaPlugin() {
    lateinit var soulsManager: SoulsManager
    lateinit var config: Config
    lateinit var vault: VaultHook
    lateinit var crates: CrateStorage
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    override fun onEnable() {
        instance = this

        config = Config.load()
        soulsManager = SoulsManager.load()
        crates = CrateStorage().loadCrates()

        CustomEnchants.register()
        SoulsPlaceholder().register()

        getCommand("redenchants")?.setExecutor(RedEnchantsCommand())
        getCommand("souls")?.setExecutor(SoulsCommand())
        getCommand("horde")?.setExecutor(HordeCommand())

        server.pluginManager.registerEvents(HordeListener(), this)
        server.pluginManager.registerEvents(GUIListener(), this)
        server.pluginManager.registerEvents(EnchantListener(), this)
        server.pluginManager.registerEvents(AirdropListener(), this)

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

        NPCRegistry().register()

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vault = VaultHook()
        }
    }

    override fun onDisable() {
        NPCRegistry().unregister()

        HordeTask.removeHorde()

        Bukkit.getScheduler().cancelTasks(this)
    }

    companion object {
        lateinit var instance: RedEnchants
            private set
    }
}
