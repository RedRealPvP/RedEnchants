package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener

object CustomEnchants {
    val TELEPATHY = TelepathyEnchant()
    val TIMBER = TimberEnchant()
    val SHOTGUN = ShotgunEnchant()
    val DECAPITATOR = DecapitatorEnchant()

    val allEnchants = listOf<Enchantment>(TELEPATHY, TIMBER, SHOTGUN, DECAPITATOR)

    fun register() {
        allEnchants.forEach { e ->
            if (e is Listener) registerListener(e)

            if (Enchantment.values().any { it == e }) return@forEach
            registerEnchant(e)
        }
    }

    private fun registerEnchant(enchant: Enchantment) {
        try {
            val f = Enchantment::class.java.getDeclaredField("acceptingNew")
            f.isAccessible = true
            f.set(null, true)
            Enchantment.registerEnchantment(enchant)
            println("Registered ${enchant.key} enchantment.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerListener(listener: Listener) {
        RedEnchants.instance.server.pluginManager.registerEvents(listener, RedEnchants.instance)
        println("Registered ${listener.javaClass.simpleName} listener.")
    }
}