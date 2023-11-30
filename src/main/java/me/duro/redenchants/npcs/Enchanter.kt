package me.duro.redenchants.npcs

import me.duro.redenchants.enchants.registry.CustomEnchants.randomEnchantBook
import me.duro.redenchants.enchants.RedEnchantRarity
import me.duro.redenchants.items.enchantedPage
import me.duro.redenchants.utils.addLore
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class Enchanter : Trait("enchanter") {
    @EventHandler
    fun onNPCRightClick(e: NPCRightClickEvent) {
        if (!e.npc.hasTrait(Enchanter::class.java)) return

        createGui(e.clicker)
    }

    private fun createGui(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, replaceColorCodes("&c&lEnchanter"))

        for (i in 0..26) {
            val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE).also {
                it.itemMeta = it.itemMeta.apply {
                    displayName(replaceColorCodes(""))
                }
            }

            inventory.setItem(i, item)
        }

        val items = RedEnchantRarity.entries.minus(RedEnchantRarity.ADMIN).map {
            addLore(
                addLore(randomEnchantBook(it), replaceColorCodes("")),
                replaceColorCodes("&7Cost: &c${it.cost()} enchanted pages")
            )
        }

        items.forEachIndexed { index, item -> inventory.setItem(9 + 2 * index, item) }

        player.openInventory(inventory)
    }

    companion object {
        fun onInventoryClick(e: InventoryClickEvent) {
            e.isCancelled = true

            val player = e.whoClicked

            val item = e.currentItem ?: return
            if (!item.itemMeta.hasLore()) return

            val cost = componentToString(
                item.itemMeta.lore()!![2]
            ).replace("&7Cost: &c", "").replace(" enchanted pages", "").toInt()

            val matchedRarity = RedEnchantRarity.entries.find { it.cost() == cost } ?: return

            val enchantedPages = player.inventory.contents.filter {
                it != null && it.hasItemMeta() && it.itemMeta.hasDisplayName() && it.itemMeta.displayName()!! == enchantedPage.itemMeta.displayName()
            }.sumOf { it?.amount ?: 0 }

            if (enchantedPages < cost) return player.sendMessage(replaceColorCodes("&cYou don't have enough enchanted pages!"))

            player.inventory.removeItem(enchantedPage.clone().apply { amount = cost })

            player.inventory.addItem(randomEnchantBook(matchedRarity))

            player.sendMessage(
                replaceColorCodes(
                    "&7You have purchased a ${matchedRarity.color()}${
                        lowerTitleCase(
                            matchedRarity.name
                        )
                    }&7 enchant for &c$cost enchanted pages&7!"
                )
            )
        }
    }
}