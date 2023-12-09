package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.airdrops.CrateList
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.componentToString
import org.bukkit.*
import org.bukkit.block.Barrel
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class AirdropListener : Listener {
    @EventHandler
    fun onEntityChangeBlock(e: EntityChangeBlockEvent) {
        val entity = e.entity as? FallingBlock ?: return

        val crate = CrateList.crateMap[entity] ?: return

        e.isCancelled = true

        crate.setBlockChest(entity.location.block)
        crate.spawnChest()
        CrateList.crateMap.remove(entity)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.inventory.type != InventoryType.BARREL) return

        val barrel = e.inventory.holder as Barrel
        val crate = CrateList.barrelList.find { it == barrel.location } ?: return
        val isEmpty = barrel.inventory.isEmpty

        val rarity =
            RedEnchantRarity.entries.find { componentToString(e.view.title()).contains(it.name, true) } ?: return

        if (isEmpty) {
            barrel.world.playEffect(barrel.location, Effect.STEP_SOUND, Material.BARREL)

            val effect = FireworkEffect.builder().trail(false).flicker(false).withColor(rarity.fireworkColor()).with(
                FireworkEffect.Type.BALL
            ).build()

            val fw = barrel.world.spawn(barrel.location.add(0.0, 1.0, 0.0), Firework::class.java).apply {
                fireworkMeta = fireworkMeta.apply {
                    clearEffects()
                    addEffect(effect)
                    power = 1
                }
            }

            Bukkit.getServer().scheduler.runTaskLater(RedEnchants.instance, Runnable { fw.detonate() }, 40)

            barrel.block.type = Material.AIR
            CrateList.barrelList.remove(crate)
        }
    }
}