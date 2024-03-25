package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget

class BejeweledEnchant : RedEnchant(
    name = "bejeweled",
    description = { "Allows you to mine crystals from the caverns." },
    canEnchant = { i -> RedEnchantTarget.PICKAXE.match(i) },
    enchantRarity = RedEnchantRarity.EXOTIC
)