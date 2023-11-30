package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack


interface InteractEnchant : EnchantType {
    fun onInteract(event: PlayerInteractEvent, entity: LivingEntity, item: ItemStack, level: Int): Boolean

    val interactPriority: EventPriority
        get() = EventPriority.NORMAL
}