package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BlockBreakEnchant
import me.duro.redenchants.enchants.types.FishingEnchant
import org.bukkit.entity.LivingEntity
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.experience
private fun experienceMultiplier(level: Int) = config.multiplierPerLevel * level
private fun experienceChance(level: Int) = config.chancePerLevel * level

class ExperienceEnchant : RedEnchant(
    name = "experience",
    description = { l -> "${(experienceChance(l) * 100).toInt()}% chance to gain ${(experienceMultiplier(l) * 100).toInt()}% more experience." },
    canEnchant = { i -> RedEnchantTarget.PICKAXE.match(i) || RedEnchantTarget.FISHING_ROD.match(i) },
    enchantRarity = RedEnchantRarity.COMMON
), BlockBreakEnchant, FishingEnchant {
    override fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (event.expToDrop <= 0 || random() > experienceChance(level)) return false

        event.expToDrop = (event.expToDrop * (1 + experienceMultiplier(level))).toInt()

        return true
    }

    override fun onFishing(event: PlayerFishEvent, item: ItemStack, level: Int): Boolean {
        if (event.expToDrop <= 0 || random() > experienceChance(level)) return false

        event.expToDrop = (event.expToDrop * (1 + experienceMultiplier(level))).toInt()

        return true
    }
}