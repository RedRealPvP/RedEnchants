package me.duro.redenchants.utils

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

fun addLore(item: ItemStack, lore: Component): ItemStack {
    if (item.itemMeta.hasLore()) {
        val newLore = item.itemMeta.lore()?.apply { add(lore) }

        item.lore(newLore)
    } else {
        item.lore(listOf(lore))
    }

    return item
}