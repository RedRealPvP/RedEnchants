package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack


interface DeathEnchant : EnchantType {
    fun onDeath(event: EntityDeathEvent, entity: LivingEntity, item: ItemStack?, level: Int): Boolean

    fun onKill(event: EntityDeathEvent, entity: LivingEntity, killer: Player, weapon: ItemStack?, level: Int): Boolean

    val deathPriority: EventPriority
        get() = EventPriority.NORMAL

    val killPriority: EventPriority
        get() = EventPriority.NORMAL
}