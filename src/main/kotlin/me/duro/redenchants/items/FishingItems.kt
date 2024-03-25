package me.duro.redenchants.items

import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.weightedRandom
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class FishingDrop(val weight: Double, val item: ItemStack) {
    BLUEGILL(0.2, ItemBuilder(Material.COD, 1, "Bluegill", listOf("&7A small and vibrant fish found in streams"), RedEnchantRarity.COMMON, 1).item),
    CARP(0.15, ItemBuilder(Material.COD, 1, "Carp", listOf("&7An adaptable fish known for its distinctive scales"), RedEnchantRarity.UNCOMMON, 2).item),
    CATFISH(0.2, ItemBuilder(Material.COD, 1, "Catfish", listOf("&7A bottom-dweller often lurking in muddy waters"), RedEnchantRarity.COMMON, 3).item),
    JELLYFISH(0.05, ItemBuilder(Material.COD, 1, "Jellyfish", listOf("&7A glowing jelly-like creature from the depths of the ocean"), RedEnchantRarity.FABULOUS, 4).item),
    PERCH(0.2, ItemBuilder(Material.COD, 1, "Perch", listOf("&7A voracious fish that darts through clear waters"), RedEnchantRarity.COMMON, 5).item),
    PIRANHA(0.05, ItemBuilder(Material.COD, 1, "Piranha", listOf("&7A fierce predator lurking in murky tropical waters"), RedEnchantRarity.RARE, 6).item),
    SMALLMOUTH_BASS(0.1, ItemBuilder(Material.COD, 1, "Smallmouth Bass", listOf("&7A fierce predator found in rocky habitats"), RedEnchantRarity.UNCOMMON, 7).item),
    TROUT(0.15, ItemBuilder(Material.COD, 1, "Trout", listOf("&7A fish known for its jumps in water streams"), RedEnchantRarity.COMMON, 8).item),
    TUNA(0.1, ItemBuilder(Material.COD, 1, "Tuna", listOf("&7A sleek predator that cruises the ocean"), RedEnchantRarity.RARE, 9).item);

    companion object {
        fun random(withVanilla: Boolean = true, withEnchantedPage: Boolean = true): ItemStack {
            val entries = entries.map { it.item to it.weight }.toMutableList()

            if (withVanilla) {
                entries.add(ItemStack(Material.COD, 1) to 0.6)
                entries.add(ItemStack(Material.SALMON, 1) to 0.25)
                entries.add(ItemStack(Material.TROPICAL_FISH, 1) to 0.02)
                entries.add(ItemStack(Material.PUFFERFISH, 1) to 0.13)
            }

            if (withEnchantedPage) entries.add(enchantedPage to 0.01)

            return weightedRandom(entries)
        }
    }
}