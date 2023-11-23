package me.duro.redenchants

import me.duro.redenchants.commands.RedEnchantsCommand
import me.duro.redenchants.enchants.CustomEnchants
import me.duro.redenchants.utils.Config
import org.bukkit.plugin.java.JavaPlugin

class RedEnchants : JavaPlugin() {

    override fun onEnable() {
        instance = this

        Config.load()

        CustomEnchants.register()

        getCommand("redenchants")?.setExecutor(RedEnchantsCommand())
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var instance: RedEnchants
            private set
    }
}
