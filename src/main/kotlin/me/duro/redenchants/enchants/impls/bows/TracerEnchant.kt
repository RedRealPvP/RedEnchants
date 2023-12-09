package me.duro.redenchants.enchants.impls.bows

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

private val config = RedEnchants.instance.config.data.tracer
private fun tracerChance(level: Int) = config.chancePerLevel * level

class TracerEnchant : RedEnchant(
    name = "tracer",
    description = { l -> "${(tracerChance(l) * 100).toInt()}% chance to shoot a glowing arrow." },
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.COMMON,
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
        if (projectile !is Arrow || victim !is Player || Math.random() > tracerChance(level)) return false

        victim.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false, false))

        return true
    }
}