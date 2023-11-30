package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.types.CombatEnchant
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.inventory.ItemStack
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.vampiric
private fun triggerChance(level: Int) = config.chancePerLevel * level
private fun healAmount(level: Int) = config.healthPerLevel * level

class VampiricEnchant : RedEnchant(
    name = "vampiric",
    description = { l ->
        "${(triggerChance(l) * 100).toInt()}% chance to heal ${healAmount(l)} HP on hit."
    },
    canEnchant = { i -> RedEnchantTarget.SWORD.match(i) },
    maxLevel = 4,
    enchantRarity = RedEnchantRarity.RARE,
), CombatEnchant {
    override fun onAttack(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean {
        val health = damager.health
        val maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

        if (health == maxHealth || triggerChance(level) < random()) return false

        val healthEvent = EntityRegainHealthEvent(
            damager, healAmount(level), EntityRegainHealthEvent.RegainReason.CUSTOM
        )
        RedEnchants.instance.server.pluginManager.callEvent(healthEvent)

        damager.health = (health + healthEvent.amount).coerceAtMost(maxHealth)

        return !healthEvent.isCancelled
    }

    override val attackPriority = EventPriority.MONITOR

    override fun onProtect(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ) = true

}