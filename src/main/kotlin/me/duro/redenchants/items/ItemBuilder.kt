package me.duro.redenchants.items

import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ItemBuilder(
    private val material: Material,
    private val amount: Int = 1,
    private val name: String = "",
    private val lore: List<String> = emptyList(),
    private val rarity: RedEnchantRarity? = null,
    private val customModelData: Int? = null,
) {
    val item = ItemStack(material, amount).also { item ->
        item.itemMeta = item.itemMeta.also { meta ->
            meta.displayName(replaceColorCodes("${rarity?.color()}$name"))

            if (customModelData != null) meta.setCustomModelData(customModelData)

            val componentLore = lore.map { replaceColorCodes(it) }

            val loreWithRarity = if (rarity != null) componentLore.plus(
                listOf(
                    replaceColorCodes(""), replaceColorCodes("${rarity.color()}&l${rarity.name}")
                )
            ) else componentLore

            meta.lore(loreWithRarity)
        }
    }
}