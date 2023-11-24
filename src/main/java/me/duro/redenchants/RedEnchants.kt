package me.duro.redenchants

import me.duro.redenchants.commands.HordeCommand
import me.duro.redenchants.commands.RedEnchantsCommand
import me.duro.redenchants.commands.SoulsCommand
import me.duro.redenchants.enchants.CustomEnchants
import me.duro.redenchants.listeners.HordeListener
import me.duro.redenchants.tasks.HordeTask
import me.duro.redenchants.utils.Config
import me.duro.redenchants.utils.SoulsManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class RedEnchants : JavaPlugin() {
    lateinit var soulsManager: SoulsManager
    lateinit var config: Config
    lateinit var hordeTask: HordeTask

    override fun onEnable() {
        instance = this

        config = Config.load()
        soulsManager = SoulsManager.load()

        CustomEnchants.register()

        getCommand("redenchants")?.setExecutor(RedEnchantsCommand())
        getCommand("souls")?.setExecutor(SoulsCommand())
        getCommand("horde")?.setExecutor(HordeCommand())

        server.pluginManager.registerEvents(HordeListener(), this)

        try {
            val world = Bukkit.getWorld("world") ?: Bukkit.getWorlds()[0]
            HordeTask(Location(world, -186.0, 88.0, 495.0), 150.0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        hordeTask.cancel()
    }

    companion object {
        lateinit var instance: RedEnchants
            private set
    }
}
