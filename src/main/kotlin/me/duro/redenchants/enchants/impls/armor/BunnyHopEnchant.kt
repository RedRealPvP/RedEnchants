package me.duro.redenchants.enchants.impls.armor

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.PassiveEnchant
import me.duro.redenchants.enchants.types.Periodic
import me.duro.redenchants.enchants.types.PeriodicImpl
import me.duro.redenchants.enchants.types.Potioned
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

class BunnyHopEnchant : RedEnchant(
    name = "bunny-hop",
    description = { l -> "Grants jump boost ${l}." },
    canEnchant = { i -> RedEnchantTarget.CHESTPLATE.match(i) },
    enchantRarity = RedEnchantRarity.EXOTIC,
    maxLevel = 2,
), Potioned, PassiveEnchant {
    override val effectType: PotionEffectType = PotionEffectType.JUMP
    override val periodImplementation: Periodic = PeriodicImpl(this)
    override fun getEffectDuration(level: Int) = 10 * 20

    override fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean {
        return addEffect(entity, level)
    }
}