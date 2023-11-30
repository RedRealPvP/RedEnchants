package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.types.BowEnchant
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.AbstractArrow.PickupStatus
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

private val config = RedEnchants.instance.config.data.shotgun
private fun spreadAngle(level: Int) = config.initialAngle - config.anglePerLevel * (level - 1)

class ShotgunEnchant : RedEnchant(
    name = "shotgun",
    description = { l -> "Shoots 5 arrows at once with a ${spreadAngle(l)}° angle." },
    maxLevel = 2,
    canEnchant = { i -> RedEnchantTarget.BOW.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS,
), BowEnchant {
    private val cooldowns = mutableMapOf<Player, Long>()

    override fun onShoot(event: EntityShootBowEvent, shooter: LivingEntity, bow: ItemStack, level: Int): Boolean {
        if (shooter !is Player) return false

        if (cooldowns.containsKey(shooter) && System.currentTimeMillis() - cooldowns[shooter]!! < 1000) {
            return false
        }

        event.isCancelled = true

        val powerLevel = bow.getEnchantmentLevel(ARROW_DAMAGE)
        val damage = event.force * (1 + powerLevel * 0.5) * config.damageMultiplier

        val enchantLevel = bow.getEnchantmentLevel(this)
        val numArrows = config.initialArrows + config.arrowsPerLevel * (enchantLevel - 1)

        for (i in 1..numArrows) {
            val direction = rotateVector(
                event.projectile.velocity, toRadians(spreadAngle(enchantLevel) * i - spreadAngle(enchantLevel) * 3)
            )
            spawnArrow(shooter.eyeLocation, direction, damage)
        }

        cooldowns[shooter] = System.currentTimeMillis()
        startCooldownTimer(shooter)

        return true
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
    ) = false
}