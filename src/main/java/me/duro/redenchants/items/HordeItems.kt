package me.duro.redenchants.items

import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val hordeFlare = ItemStack(Material.REDSTONE_TORCH).also {
    it.itemMeta = it.itemMeta.apply {
        displayName(replaceColorCodes("&c&lHorde Flare"))
        lore(listOf(replaceColorCodes("&7Right click to spawn a horde.")))
        setCustomModelData(1)
    }
}
