package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack

interface DamageEnchant : EnchantType {
    fun onDamage(event: EntityDamageEvent, entity: LivingEntity, item: ItemStack, level: Int): Boolean

    val damagePriority: EventPriority
        get() = EventPriority.NORMAL
}