package me.duro.redenchants.enchants.impls.fishing

import me.duro.redenchants.enchants.registry.RedEnchant
import me.duro.redenchants.enchants.registry.RedEnchantRarity
import me.duro.redenchants.enchants.registry.RedEnchantTarget
import me.duro.redenchants.enchants.types.FishingEnchant
import org.bukkit.Sound
import org.bukkit.entity.Item
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private fun doubleChance(level: Int) = level * 0.01

class DoubleCatchEnchant : RedEnchant(
    name = "double-catch",
    description = { l -> "${(doubleChance(l) * 100).toInt()}% chance to double drops." },
    canEnchant = { i -> RedEnchantTarget.FISHING_ROD.match(i) },
    maxLevel = 3,
    enchantRarity = RedEnchantRarity.UNCOMMON,
), FishingEnchant {
    override fun onFishing(event: PlayerFishEvent, item: ItemStack, level: Int): Boolean {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH || random() < doubleChance(level)) return false

        val drop = event.caught
        if (drop !is Item) return false

        val stack = (event.caught as Item).itemStack
        stack.amount = stack.getMaxStackSize().coerceAtMost(stack.amount * 2)
        event.player.playSound(event.player.location, Sound.BLOCK_AMETHYST_BLOCK_HIT, 1f, 1f)

        drop.itemStack = stack

        return true
    }
}