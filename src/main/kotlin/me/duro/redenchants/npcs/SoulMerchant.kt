package me.duro.redenchants.npcs

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.items.limitedTimeOffers
import me.duro.redenchants.utils.addLore
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.replaceColorCodes
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class SoulMerchant : Trait("soul_merchant") {
    @EventHandler
    fun onNPCRightClick(e: NPCRightClickEvent) {
        if (!e.npc.hasTrait(SoulMerchant::class.java)) return

        createGui(e.clicker)
    }

    private fun createGui(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, replaceColorCodes("&c&lSoul Merchant"))

        for (i in 0..26) {
            val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE).also {
                it.itemMeta = it.itemMeta.apply {
                    displayName(replaceColorCodes(""))
                }
            }

            inventory.setItem(i, item)
        }

        limitedTimeOffers.forEachIndexed { index, offer ->
            inventory.setItem(
                index + 10, addLore(
                    addLore(offer.item.clone(), replaceColorCodes("")), replaceColorCodes("&7Cost: &c${offer.cost}")
                )
            )
        }

        val soulStar = ItemStack(Material.NETHER_STAR).also {
            it.itemMeta = it.itemMeta.apply {
                displayName(replaceColorCodes("&c&lSouls"))
                lore(listOf(replaceColorCodes("&7You have &c${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)} &7souls!")))
            }
        }

        inventory.setItem(22, soulStar)

        player.openInventory(inventory)
    }

    companion object {
        fun onInventoryClick(e: InventoryClickEvent) {
            e.isCancelled = true

            if (e.currentItem == null || e.currentItem?.itemMeta?.hasDisplayName() == null) return

            val offer = limitedTimeOffers.find {
                it.item.itemMeta.displayName() == e.currentItem!!.itemMeta.displayName()
            } ?: return

            val player = e.whoClicked

            if (player.inventory.firstEmpty() == -1) {
                return player.sendMessage(replaceColorCodes("&cYour inventory is full!"))
            }

            if (RedEnchants.instance.soulsManager.getSouls(player.uniqueId) < offer.cost) {
                return player.sendMessage(replaceColorCodes("&cYou don't have enough souls!"))
            }

            RedEnchants.instance.soulsManager.removeSouls(player.uniqueId, offer.cost)
            player.inventory.addItem(offer.item)

            val displayName = componentToString(offer.item.itemMeta.displayName()!!)
            player.sendMessage(replaceColorCodes("&7You have purchased $displayName&7 for &c${offer.cost}&7 souls!"))
        }
    }
}