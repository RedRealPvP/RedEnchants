package me.duro.redenchants.enchants.impls.weapons

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.DeathEnchant
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.scavenger

private fun scavengerChance(level: Int) = config.chancePerLevel * level
private fun scavengerAmount(level: Int) = config.amountPerLevel * level

class ScavengerEnchant : RedEnchant(
    name = "scavenger",
    description = { l -> "${(scavengerChance(l) * 100).toInt()}% chance to gain $${scavengerAmount(l)} on killing entities." },
    canEnchant = { i -> RedEnchantTarget.AXE.match(i) || RedEnchantTarget.SWORD.match(i) },
    enchantRarity = RedEnchantRarity.EXOTIC,
    maxLevel = 5
), DeathEnchant {
    override fun onKill(
        event: EntityDeathEvent, entity: LivingEntity, killer: Player, weapon: ItemStack?, level: Int
    ): Boolean {
        if (random() > scavengerChance(level)) return false

        RedEnchants.instance.vault.deposit(killer, scavengerAmount(level))
        killer.playSound(killer.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)

        return true
    }

    override fun onDeath(event: EntityDeathEvent, entity: LivingEntity, item: ItemStack?, level: Int) = false
}