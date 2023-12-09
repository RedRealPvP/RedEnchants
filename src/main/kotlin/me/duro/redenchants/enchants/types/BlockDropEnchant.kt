package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack

interface BlockDropEnchant : EnchantType {
    fun onDrop(event: BlockDropItemEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean

    val dropPriority: EventPriority
        get() = EventPriority.NORMAL
}