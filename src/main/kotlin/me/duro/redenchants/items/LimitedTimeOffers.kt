package me.duro.redenchants.items

import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class Offer(val item: ItemStack, val cost: Int)

val enchantedPage = ItemStack(Material.PAPER).apply {
    addUnsafeEnchantment(Enchantment.WATER_WORKER, 1)

    itemMeta = itemMeta.apply {
        displayName(replaceColorCodes("&c&lEnchanted Page"))
        lore(listOf(replaceColorCodes("&7Redeem at the &cEnchanter &7to enchant an item.")))
        setCustomModelData(1)
        addItemFlags(ItemFlag.HIDE_ENCHANTS)
    }
}

val limitedTimeOffers = listOf(
    Offer(enchantedPage.clone(), 5),
    Offer(ItemStack(Material.DIAMOND_HELMET).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Helmet"))
        }
    }, 7),
    Offer(ItemStack(Material.DIAMOND_CHESTPLATE).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Chestplate"))
        }
    }, 15),
    Offer(ItemStack(Material.DIAMOND_LEGGINGS).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Leggings"))
        }
    }, 10),
    Offer(ItemStack(Material.DIAMOND_BOOTS).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Boots"))
        }
    }, 5),
    Offer(ItemStack(Material.DIAMOND_PICKAXE).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Pickaxe"))
        }
    }, 8),
    Offer(ItemStack(Material.DIAMOND_AXE).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("&c&lSoul Axe"))
        }
    }, 10),
)