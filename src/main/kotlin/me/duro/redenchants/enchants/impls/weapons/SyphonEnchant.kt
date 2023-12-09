package me.duro.redenchants.enchants.impls.weapons

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.CombatEnchant
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.syphon
private fun triggerChance(level: Int) = config.chancePerLevel * level
private fun healAmount(level: Int) = config.healthPerLevel * level

class SyphonEnchant : RedEnchant(
    name = "syphon",
    description = { l ->
        "${(triggerChance(l) * 100).toInt()}% chance to heal ${healAmount(l)} HP on hit."
    },
    canEnchant = { i -> RedEnchantTarget.SWORD.match(i) },
    enchantRarity = RedEnchantRarity.RARE,
    maxLevel = 4,
), CombatEnchant {
    override fun onAttack(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean {
        if (damager !is Player || victim !is Player) return false

        val health = damager.health
        val maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

        if (health == maxHealth || triggerChance(level) < random()) return false

        val healthEvent = EntityRegainHealthEvent(
            damager, healAmount(level), EntityRegainHealthEvent.RegainReason.CUSTOM
        )

        RedEnchants.instance.server.pluginManager.callEvent(healthEvent)

        damager.health = (health + healthEvent.amount).coerceAtMost(maxHealth)
        damager.playSound(damager.location, Sound.ENTITY_SPLASH_POTION_BREAK, 1f, 1f)

        return !healthEvent.isCancelled
    }

    override val attackPriority = EventPriority.MONITOR

    override fun onProtect(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ) = true

}