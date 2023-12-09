package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

interface BowEnchant : EnchantType {
    fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int): Boolean

    fun onHit(
        event: ProjectileHitEvent, shooter: LivingEntity, projectile: Projectile, bow: ItemStack, level: Int
    ): Boolean

    fun onDamage(
        event: EntityDamageByEntityEvent,
        projectile: Projectile,
        shooter: LivingEntity,
        victim: LivingEntity,
        weapon: ItemStack,
        level: Int
    ): Boolean

    val shootPriority: EventPriority
        get() = EventPriority.NORMAL

    val hitPriority: EventPriority
        get() = EventPriority.NORMAL

    val damagePriority: EventPriority
        get() = EventPriority.NORMAL
}