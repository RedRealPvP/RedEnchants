package me.duro.redenchants.enchants.impls.bows

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.Math.random

private val config = RedEnchants.instance.config.data.flashbang
private fun flashChance(level: Int) = config.chancePerLevel * level
private fun flashSecs(level: Int) = config.durationPerLevel * level

class FlashbangEnchant : RedEnchant(
    name = "flashbang",
    description = { l -> "${(flashChance(l) * 100).toInt()}% chance to freeze enemies for ${flashSecs(l)}s." },
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.UNCOMMON,
    maxLevel = 4,
), BowEnchant {
    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int) = false

    override fun onHit(
        event: ProjectileHitEvent, shooter: LivingEntity, projectile: Projectile, bow: ItemStack, level: Int
    ) = false

    override fun onDamage(
        event: EntityDamageByEntityEvent,
        projectile: Projectile,
        shooter: LivingEntity,
        victim: LivingEntity,
        weapon: ItemStack,
        level: Int
    ): Boolean {
        if (projectile !is Arrow || victim !is Player || random() > flashChance(level)) return false

        victim.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * flashSecs(level), 1))
        victim.playSound(victim.location, Sound.ENTITY_ENDERMAN_DEATH, 1f, 1f)

        return true
    }
}