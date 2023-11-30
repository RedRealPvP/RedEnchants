package me.duro.redenchants.listeners

import me.duro.redenchants.npcs.Enchanter
import me.duro.redenchants.npcs.SoulMerchant
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIListener : Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        when (e.view.title()) {
            replaceColorCodes("&c&lSoul Merchant") -> SoulMerchant.onInventoryClick(e)
            replaceColorCodes("&c&lEnchanter") -> Enchanter.onInventoryClick(e)
        }
    }
}