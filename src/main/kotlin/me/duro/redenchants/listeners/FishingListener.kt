package me.duro.redenchants.listeners

import me.duro.redenchants.items.FishingDrop
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent


class FishingListener : Listener {
   @EventHandler(priority = EventPriority.HIGH)
   private fun onPlayerFish(e: PlayerFishEvent) {
        if (e.state == PlayerFishEvent.State.CAUGHT_FISH) {
            if (e.caught !is Item) return

            val fish = FishingDrop.random(withVanilla = false, withEnchantedPage = false)
            (e.caught as Item).itemStack = fish
        }
   }
}