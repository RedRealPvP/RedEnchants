package me.duro.redenchants.enchants.impls.bows

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private fun freezeChance(level: Int) = level * 0.05
private fun freezeSecs(level: Int) = level * 5

class FrostbiteEnchant : RedEnchant(
    name = "frostbite",
    description = { l -> "${(freezeChance(l) * 100).toInt()}% chance to freeze enemies for ${freezeSecs(l)}s." },
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.UNCOMMON,
    maxLevel = 3,
), BowEnchant {
    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int) = false

    override fun onHit(
        event: ProjectileHitEvent, shooter: LivingEntity, projectile: Projectile, bow: ItemStack, level: Int
    ) = false

    override fun onDamage(
        event: EntityDamageByEntityEvent,
        projectile: Projectile,
        shooter: LivingEntity,
        victim: LivingEntity,
        weapon: ItemStack,
        level: Int
    ): Boolean {
        if (projectile !is Arrow || random() > freezeChance(level)) return false

        victim.freezeTicks = Integer.MAX_VALUE

        RedEnchants.instance.server.scheduler.runTaskLater(RedEnchants.instance, Runnable {
            victim.freezeTicks = 0
        }, (freezeSecs(level) * 20).toLong() * 2)

        val particleCount = 20

        for (i in 0 until particleCount) {
            val fadeFactor = (i.toDouble() / particleCount.toDouble()) * 0.5

            val red = 128
            val green = (1.0 - fadeFactor) * 255
            val blue = 255

            val color = Color.fromRGB(red, green.toInt(), blue)

            victim.world.spawnParticle(
                Particle.REDSTONE,
                victim.eyeLocation.add(0.0, 1.0, 0.0),
                1,
                0.25,
                0.25,
                0.25,
                0.1,
                Particle.DustOptions(color, 1.0f)
            )
        }

        return true
    }
}