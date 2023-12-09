package me.duro.redenchants.airdrops

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

fun airdropGUI(rarity: RedEnchantRarity): Inventory {

    val inventory = Bukkit.createInventory(
        null, 27, replaceColorCodes("${rarity.color()}${lowerTitleCase(rarity.name)}&7 Airdrop Editor")
    )

    val items = RedEnchants.instance.crates.getCrate(rarity)

    items.forEach {
        if (it != null) inventory.addItem(it)
    }

    return inventory
}