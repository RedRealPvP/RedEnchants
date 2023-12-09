package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.*
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

class HasteEnchant : RedEnchant(
    name = "haste",
    description = { l -> "Grants haste $l effect." },
    canEnchant = { i -> RedEnchantTarget.TOOL.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS,
), Potioned, PassiveEnchant {
    override val effectType: PotionEffectType = PotionEffectType.FAST_DIGGING
    override val periodImplementation: Periodic = PeriodicImpl(this)
    override fun getEffectDuration(level: Int) = 10 * 20

    override fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean {
        return addEffect(entity, level)
    }
}