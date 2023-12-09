package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.npcs.Enchanter
import me.duro.redenchants.npcs.SoulMerchant
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUIListener : Listener {
    private val crates = RedEnchants.instance.crates

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        when (e.view.title()) {
            replaceColorCodes("&c&lSoul Merchant") -> SoulMerchant.onInventoryClick(e)
            replaceColorCodes("&c&lEnchanter") -> Enchanter.onInventoryClick(e)
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val title = componentToString(e.view.title())

        val rarity = RedEnchantRarity.entries.find {
            title.contains("${it.color()}${it.name}", true)
        } ?: return

        if (!title.contains("Airdrop Editor", true)) return

        crates.setCrate(rarity, e.inventory)
    }
}