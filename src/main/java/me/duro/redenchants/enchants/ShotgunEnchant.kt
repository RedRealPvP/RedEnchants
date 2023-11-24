package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.AbstractArrow.PickupStatus
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class ShotgunEnchant : RedEnchant(
    name = "shotgun",
    maxLevel = 2,
    itemTarget = EnchantmentTarget.BOW,
    canEnchant = { i -> EnchantmentTarget.BOW.includes(i) },
), Listener {
    private val config = RedEnchants.instance.config.data.shotgun
    private val cooldowns = mutableMapOf<Player, Long>()

    @EventHandler
    fun onBowShoot(e: EntityShootBowEvent) {
        val player = e.entity

        if (player !is Player) return

        if (cooldowns.containsKey(player) && System.currentTimeMillis() - cooldowns[player]!! < 1000) {
            return
        }

        val item = player.inventory.itemInMainHand
        if (!item.enchantments.containsKey(this)) return

        e.isCancelled = true

        val powerLevel = item.getEnchantmentLevel(ARROW_DAMAGE)
        val damage = e.force * (1 + powerLevel * 0.5) * config.damageMultiplier

        val enchantLevel = item.getEnchantmentLevel(this)
        val spreadAngle = config.initialAngle - config.anglePerLevel * (enchantLevel - 1)

        val numArrows = config.initialArrows + config.arrowsPerLevel * (enchantLevel - 1)

        for (i in 1..numArrows) {
            val direction = rotateVector(e.projectile.velocity, Math.toRadians(spreadAngle * i - spreadAngle * 3))
            spawnArrow(player.eyeLocation, direction, damage)
        }

        cooldowns[player] = System.currentTimeMillis()
        startCooldownTimer(player)
    }

    private fun rotateVector(vector: Vector, angle: Double): Vector {
        val x = vector.x * cos(angle) - vector.z * sin(angle)
        val z = vector.x * sin(angle) + vector.z * cos(angle)
        return vector.setX(x).setZ(z)
    }

    private fun spawnArrow(location: Location, direction: Vector, damage: Double) {
        val arrow = location.world.spawnArrow(location, direction, 1.0F, 0F)
        arrow.pickupStatus = PickupStatus.CREATIVE_ONLY
        arrow.damage = damage
    }

    private fun startCooldownTimer(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                cooldowns.remove(player)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.25f, 1.0f)
            }
        }.runTaskLater(RedEnchants.instance, config.cooldown * 20L)
    }
}