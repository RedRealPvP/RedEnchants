package me.duro.redenchants.enchants.types

import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

interface FishingEnchant : EnchantType {
    fun onFishing(event: PlayerFishEvent, item: ItemStack, level: Int): Boolean

    val fishingPriority: EventPriority
        get() = EventPriority.NORMAL
}