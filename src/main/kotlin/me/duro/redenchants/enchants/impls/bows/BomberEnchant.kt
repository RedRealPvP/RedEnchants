package me.duro.redenchants.enchants.impls.bows

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.bomber
private fun explodeChance(level: Int) = config.chancePerLevel * level
private fun tntForce(level: Int) = config.forcePerLevel * level

class BomberEnchant : RedEnchant(
    name = "bomber",
    description = { l -> "${(explodeChance(l) * 100).toInt()}% to launch TNT on shoot." },
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.RARE,
    conflictEnchants = listOf(CustomEnchants.SHOTGUN, CustomEnchants.BULLET),
    maxLevel = 3,
), BowEnchant {
    override val shootPriority = EventPriority.LOWEST

    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int): Boolean {
        if (random() > explodeChance(level) || event.projectile !is Projectile) return false

        val primed = event.projectile.world.spawn(event.projectile.location, TNTPrimed::class.java)

        primed.velocity = event.projectile.velocity.multiply(event.force * tntForce(level))
        primed.fuseTicks = 20
        primed.source = null
        event.projectile = primed

        return true
    }

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
    ) = false
}