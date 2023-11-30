package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

interface BlockBreakEnchant : EnchantType {
    fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean

    val breakPriority: EventPriority
        get() = EventPriority.HIGH
}