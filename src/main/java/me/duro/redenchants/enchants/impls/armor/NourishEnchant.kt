package me.duro.redenchants.enchants.impls.armor

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.PassiveEnchant
import me.duro.redenchants.enchants.types.Periodic
import me.duro.redenchants.enchants.types.PeriodicImpl
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private val config = RedEnchants.instance.config.data.nourish
private fun saturationAmount(level: Int) = config.amountPerLevel * level

class NourishEnchant : RedEnchant(
    name = "nourish",
    description = { l -> "Restores ${saturationAmount(l)} hunger every 10 seconds." },
    canEnchant = { i -> RedEnchantTarget.HELMET.match(i) },
    enchantRarity = RedEnchantRarity.RARE,
    maxLevel = 4,
), PassiveEnchant {
    override val periodImplementation: Periodic = PeriodicImpl(this, 10)

    override fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (entity !is Player || entity.foodLevel >= 20) return false

        entity.foodLevel += 20.coerceAtMost(saturationAmount(level))
        entity.playSound(entity.location, Sound.ENTITY_PLAYER_BURP, 1f, 1f)

        return true
    }
}