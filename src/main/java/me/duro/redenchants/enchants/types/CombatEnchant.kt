package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

interface CombatEnchant : EnchantType {
    fun onAttack(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean

    fun onProtect(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean

    val attackPriority: EventPriority
        get() = EventPriority.NORMAL

    val protectPriority: EventPriority
        get() = EventPriority.NORMAL
}