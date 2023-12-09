package me.duro.redenchants.enchants.impls.weapons

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BowEnchant
import me.duro.redenchants.enchants.types.CombatEnchant
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.thunder
private fun thunderChance(level: Int) = config.chancePerLevel * level

class ThunderEnchant : RedEnchant(
    name = "thunder",
    description = { l -> "${(thunderChance(l) * 100).toInt()}% chance to strike lightning on hit." },
    canEnchant = { i -> RedEnchantTarget.AXE.match(i) || RedEnchantTarget.SWORD.match(i) || RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS,
    maxLevel = 3
), CombatEnchant, BowEnchant {
    override fun onHit(
        event: ProjectileHitEvent, shooter: LivingEntity, projectile: Projectile, bow: ItemStack, level: Int
    ): Boolean {
        if (random() > thunderChance(level) || event.hitEntity !is Player) return false

        projectile.world.strikeLightningEffect((event.hitEntity as Player).location)

        return true
    }

    override fun onAttack(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean {
        if (random() > thunderChance(level)) return false

        victim.world.strikeLightningEffect(victim.location)

        return true
    }

    override fun onDamage(
        event: EntityDamageByEntityEvent,
        projectile: Projectile,
        shooter: LivingEntity,
        victim: LivingEntity,
        weapon: ItemStack,
        level: Int
    ) = false


    override fun onProtect(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ) = false

    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int) = false
}