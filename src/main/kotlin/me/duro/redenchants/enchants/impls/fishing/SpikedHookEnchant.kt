package me.duro.redenchants.enchants.impls.fishing

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.FishingEnchant
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

private val config = RedEnchants.instance.config.data.spikedHook
private fun spikedHookDamage(level: Int) = config.damagePerLevel * level

class SpikedHookEnchant : RedEnchant(
    name = "spiked-hook",
    description = { l -> "Deal ${spikedHookDamage(l)} damage on hook." },
    canEnchant = { i -> RedEnchantTarget.FISHING_ROD.match(i) },
    enchantRarity = RedEnchantRarity.RARE,
    maxLevel = 5,
), FishingEnchant {
    override fun onFishing(event: PlayerFishEvent, item: ItemStack, level: Int): Boolean {
        if (event.state != PlayerFishEvent.State.CAUGHT_ENTITY) return false

        val entity = event.caught ?: return false
        if (entity !is LivingEntity) return false

        entity.damage(spikedHookDamage(level), event.player)

        return true
    }
}