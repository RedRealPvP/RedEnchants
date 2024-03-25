package me.duro.redenchants.listeners

import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.items.Crystal
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.lang.Math.random

class CrystalListener : Listener {
    private val hitMap = mutableMapOf<ArmorStand, Int>()
    private val cooldowns = mutableMapOf<ArmorStand, Long>()

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val entity = e.entity
        val player = e.damager

        if (entity !is ArmorStand || player !is Player) return

        val item = player.inventory.itemInMainHand
        val helmet = entity.equipment.helmet ?: return
        val crystalType = Crystal.typeFromItem(helmet) ?: return

        if (!item.containsEnchantment(CustomEnchants.BEJEWELED)) {
            e.isCancelled = true
            return
        }

        val crystal = Crystal(crystalType)

        val hitCount = hitMap.getOrDefault(entity, 0)
        hitMap[entity] = hitCount + 1

        if (cooldowns.containsKey(entity) && System.currentTimeMillis() - cooldowns[entity]!! < 1000) {
            e.isCancelled = true
            return
        }

        cooldowns[entity] = System.currentTimeMillis()

        player.world.spawnParticle(Particle.WAX_OFF, entity.eyeLocation, 10, 0.5, 0.25, 0.25, 0.25)
        addFragment(player)

        if (hitCount > 2) {
            player.world.playSound(player.eyeLocation, Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1f, 1f)

            entity.remove()

            val gemItem = if (random() > 0.05) crystal.gemstoneItem else crystal.enchantedGemstoneItem

            entity.world.spawn(entity.eyeLocation, ExperienceOrb::class.java).experience = 5
            entity.world.dropItemNaturally(entity.eyeLocation, gemItem)

            hitMap.remove(entity)
            return
        }

        player.playSound(player.eyeLocation, Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1f, 1f)
    }

    private fun addFragment(player: Player) {
        if (random() < 0.30) player.inventory.addItem(Crystal.fragmentItem)
    }
}