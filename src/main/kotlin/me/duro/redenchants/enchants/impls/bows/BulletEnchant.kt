package me.duro.redenchants.enchants.impls.bows

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

private val config = RedEnchants.instance.config.data.bullet
private fun arrowSpeed(level: Int) = config.speedPerLevel * level

class BulletEnchant : RedEnchant(
    name = "bullet",
    description = { l -> "Arrows travel ${(arrowSpeed(l) * 100).toInt()}% faster." },
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.COMMON,
    conflictEnchants = listOf(CustomEnchants.SHOTGUN, CustomEnchants.BOMBER),
    maxLevel = 3,
), BowEnchant {
    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int): Boolean {
        if (event.projectile !is Arrow) return false

        event.projectile.velocity = event.projectile.velocity.multiply(1 + arrowSpeed(level))

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