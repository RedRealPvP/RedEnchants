package me.duro.redenchants.enchants.impls.weapons

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.CombatEnchant
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.lang.Math.random
import kotlin.math.cos
import kotlin.math.sin

private val config = RedEnchants.instance.config.data.cleave
private fun cleaveRadius(level: Int) = config.radiusPerLevel * level
private fun cleaveChance(level: Int) = config.chancePerLevel * level

class CleaveEnchant : RedEnchant(
    name = "cleave",
    description = { l -> "${(cleaveChance(l) * 100).toInt()}% chance to sweep attack with radius of ${cleaveRadius(l)} blocks." },
    canEnchant = { i -> RedEnchantTarget.SWORD.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS,
    maxLevel = 4
), CombatEnchant {
    override fun onAttack(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ): Boolean {
        if (random() > cleaveChance(level)) return false

        val entities = damager.getNearbyEntities(
            cleaveRadius(level).toDouble(), cleaveRadius(level).toDouble(), cleaveRadius(level).toDouble()
        )

        entities.forEach { entity ->
            if (entity is LivingEntity && entity != victim) {
                entity.damage(event.damage, damager)
            }
        }

        spawnCriticalParticles(damager as Player, cleaveRadius(level).toDouble())

        return true
    }

    private fun spawnCriticalParticles(player: Player, radius: Double) {
        object : BukkitRunnable() {
            val vec = player.eyeLocation.subtract(0.0, 0.5, 0.0).direction.multiply(0.4)
            val loc = player.eyeLocation.subtract(0.0, 0.5, 0.0)
            var t = 0

            override fun run() {
                if (t++ > radius ) {
                    cancel()
                    return
                }

                for (j in 0..2) {
                    loc.add(vec)

                    if (loc.block.type.isSolid) {
                        cancel()
                        break
                    }

                    val angle = t.toDouble() / 3
                    val vec = rotateFunc(Vector(cos(angle), sin(angle), 0.0).multiply(0.3), loc)

                    player.world.spawnParticle(
                        org.bukkit.Particle.CRIT,
                        loc.clone().add(vec),
                        1,
                        vec.x,
                        vec.y,
                        vec.z,
                        0.0
                    )

                    player.world.spawnParticle(
                        org.bukkit.Particle.CRIT,
                        loc.clone().add(vec.multiply(-1)),
                        1,
                        vec.x,
                        vec.y,
                        vec.z,
                        0.0
                    )
                }
            }
        }.runTaskTimer(RedEnchants.instance, 0, 1)
    }

    private fun rotateFunc(vec: Vector, loc: Location): Vector {
        var v = vec
        val yaw = loc.yaw / 180 * Math.PI
        val pitch = loc.pitch / 180 * Math.PI
        v = rotAxisX(v, pitch)
        v = rotAxisY(v, -yaw)
        return v
    }

    private fun rotAxisX(v: Vector, a: Double): Vector {
        val y = v.y * cos(a) - v.z * sin(a)
        val z = v.y * sin(a) + v.z * cos(a)
        return v.setY(y).setZ(z)
    }

    private fun rotAxisY(v: Vector, b: Double): Vector {
        val x = v.x * cos(b) + v.z * sin(b)
        val z = v.x * -sin(b) + v.z * cos(b)
        return v.setX(x).setZ(z)
    }

    override fun onProtect(
        event: EntityDamageByEntityEvent, damager: LivingEntity, victim: LivingEntity, weapon: ItemStack, level: Int
    ) = false
}