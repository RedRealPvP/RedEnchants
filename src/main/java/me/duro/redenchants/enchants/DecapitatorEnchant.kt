package me.duro.redenchants.enchants

import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.Listener

class DecapitatorEnchant : RedEnchant(
    name = "decapitator",
    itemTarget = EnchantmentTarget.WEAPON,
    canEnchant = { i -> EnchantmentTarget.WEAPON.includes(i) },
), Listener {

}