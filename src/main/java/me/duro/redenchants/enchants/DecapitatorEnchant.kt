package me.duro.redenchants.enchants

import me.duro.redenchants.utils.Config
import org.bukkit.Material
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class DecapitatorEnchant : RedEnchant(
    name = "decapitator",
    itemTarget = EnchantmentTarget.WEAPON,
    canEnchant = { i -> EnchantmentTarget.WEAPON.includes(i) },
), Listener {
    private val config = Config.load().data.decapitator

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (e.entity.killer !is Player || e.entity !is Player) return

        val player = e.entity.killer as Player
        val victim = e.entity as Player
        val item = player.inventory.itemInMainHand

        if (!item.enchantments.containsKey(this) || item.enchantments.containsKey(CustomEnchants.TELEPATHY)) return

        if (Math.random() < config.dropChance) {
            val skull = ItemStack(Material.PLAYER_HEAD)
            val meta = (skull.itemMeta as SkullMeta).apply { owningPlayer = victim }
            skull.itemMeta = meta

            e.drops.add(skull)
        }
    }
}