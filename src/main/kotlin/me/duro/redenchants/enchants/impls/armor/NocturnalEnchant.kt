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

class NocturnalEnchant : RedEnchant(
    name = "nocturnal",
    description = { "Grants night vision effect." },
    canEnchant = { i -> RedEnchantTarget.HELMET.match(i) },
    enchantRarity = RedEnchantRarity.UNCOMMON,
), Potioned, PassiveEnchant {
    override val effectType: PotionEffectType = PotionEffectType.NIGHT_VISION
    override val periodImplementation: Periodic = PeriodicImpl(this)
    override val isPermanent = true

    override fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean {
        return addEffect(entity, level)
    }
}