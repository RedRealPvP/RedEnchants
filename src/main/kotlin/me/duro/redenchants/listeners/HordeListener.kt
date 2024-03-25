package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.items.hordeFlare
import me.duro.redenchants.tasks.HordeTask
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.lang.Math.random
import kotlin.math.pow

class HordeListener : Listener {
    private val config = RedEnchants.instance.config.data.horde

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (e.entity.killer !is Player || !isHordeMob(e.entity)) return

        val entity = e.entity
        val player = e.entity.killer!!

        if (isHordeMob(entity)) {
            e.droppedExp = 0
            e.drops.clear()

            val drop = (entity.equipment!!.armorContents.toList() + entity.equipment!!.itemInMainHand).random()

            entity.world.dropItemNaturally(entity.location, drop)
            player.world.playSound(entity.location, Sound.ENTITY_STRAY_DEATH, 1.0f, 1.0f)
            player.world.spawnParticle(Particle.SOUL, entity.location, 75, 0.5, 1.0, 0.5, 0.0)

            RedEnchants.instance.soulsManager.addSouls(e.entity.killer!!.uniqueId, 1)

            player.sendMessage(
                replaceColorCodes(
                    config.messages.kill.replace("{souls}", "1")
                        .replace("{total}", "${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}")
                )
            )
        }
    }

    @EventHandler
    fun onCraftPrepare(e: PrepareItemCraftEvent) {
        val items = e.inventory.matrix

        if (items.any { it != null && it.itemMeta.hasCustomModelData() && it.itemMeta.customModelData == 1 }) {
            e.inventory.result = null
        }
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        val item = e.item ?: return

        if (hordeFlare.isSimilar(item)) {
            e.isCancelled = true

            val player = e.player
            item.subtract(1)

            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f)

            val center = Location(
                Bukkit.getWorld(config.spawn.world) ?: Bukkit.getWorlds()[0],
                config.spawn.center.first,
                config.spawn.center.second,
                config.spawn.center.third
            )

            HordeTask(
                center,
                config.spawn.radius,
            )
        }
    }

    companion object {
        val hordeKey = NamespacedKey(RedEnchants.instance, "is_horde_mob")

        private val hordeMobTypes = listOf(
            EntityType.ZOMBIE,
            EntityType.HUSK,
            EntityType.SKELETON,
            EntityType.STRAY,
        )

        fun isHordeMob(entity: Entity): Boolean {
            return hordeMobTypes.contains(entity.type) && entity.persistentDataContainer.getOrDefault(
                hordeKey, PersistentDataType.BYTE, 0.toByte()
            ) == 1.toByte()
        }

        private fun setHordeMob(entity: Entity) {
            entity.persistentDataContainer.set(hordeKey, PersistentDataType.BYTE, 1.toByte())
        }

        fun spawnHorde(location: Location) {
            val hordeSize = (2..5).random()
            val hordeRadius = 3.0

            for (i in 0..hordeSize) {
                val x = (location.x + (random() * hordeRadius * 2) - hordeRadius).toInt()
                val z = (location.z + (random() * hordeRadius * 2) - hordeRadius).toInt()
                val y = location.world.getHighestBlockYAt(x, z)

                spawnHordeMob(
                    Location(location.world, x.toDouble(), y.toDouble() + 2, z.toDouble()), i == 0
                )
            }
        }

        private fun spawnHordeMob(location: Location, isLeader: Boolean = false) {
            val mobType = hordeMobTypes.random()
            val mob = location.world.spawnEntity(location, mobType) as LivingEntity

            mob.removeWhenFarAway = false
            mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.3

            if (isLeader) {
                val taskIds = mutableListOf<Int>()

                val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RedEnchants.instance, {
                    mob.world.spawnParticle(Particle.SOUL_FIRE_FLAME, mob.location, 75, 0.5, 1.0, 0.5, 0.0)

                    if (mob.isDead) {
                        taskIds.forEach { Bukkit.getScheduler().cancelTask(it) }
                    }
                }, 0, 20)

                taskIds.add(taskId)
            }

            giveEnchantedArmor(mob, Material.DIAMOND_HELMET)
            giveEnchantedArmor(mob, Material.DIAMOND_CHESTPLATE)
            giveEnchantedArmor(mob, Material.DIAMOND_LEGGINGS)
            giveEnchantedArmor(mob, Material.DIAMOND_BOOTS)

            if (listOf(EntityType.STRAY, EntityType.SKELETON).contains(mobType)) {
                giveEnchantedBow(mob)
            } else {
                giveEnchantedWeapon(mob)
            }

            setHordeMob(mob)
        }

        fun generateHordeLocations(
            center: Location, innerRadius: Double, outerRadius: Double, amount: Int
        ): List<Location> {
            val locations = mutableListOf<Location>()

            repeat(amount) {
                var x: Double
                var z: Double
                var y: Double

                do {
                    x = center.x + random() * outerRadius * 2 - outerRadius
                    z = center.z + random() * outerRadius * 2 - outerRadius
                    y = center.world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble()
                } while (y > 170 || isWithinInnerRadius(center, x, z, innerRadius))

                locations.add(Location(center.world, x, y, z))
            }

            return locations
        }

        private fun isWithinInnerRadius(center: Location, x: Double, z: Double, innerRadius: Double): Boolean {
            val distanceSquared = (x - center.x).pow(2) + (z - center.z).pow(2)
            return distanceSquared < innerRadius.pow(2)
        }

        private fun giveEnchantedArmor(entity: LivingEntity, material: Material) {
            val armor = ItemStack(material)

            val protectionLevel = (2..4).random()
            val unbreakingLevel = (1..3).random()

            armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel)
            armor.addEnchantment(Enchantment.DURABILITY, unbreakingLevel)

            when (armor.type) {
                Material.DIAMOND_HELMET -> entity.equipment?.helmet = armor
                Material.DIAMOND_CHESTPLATE -> entity.equipment?.chestplate = armor
                Material.DIAMOND_LEGGINGS -> entity.equipment?.leggings = armor
                Material.DIAMOND_BOOTS -> entity.equipment?.boots = armor
                else -> return
            }
        }

        private fun giveEnchantedWeapon(entity: LivingEntity) {
            val weapon = ItemStack(Material.DIAMOND_SWORD)

            val sharpnessLevel = (2..4).random()
            val unbreakingLevel = (1..3).random()

            weapon.addEnchantment(Enchantment.DAMAGE_ALL, sharpnessLevel)
            weapon.addEnchantment(Enchantment.DURABILITY, unbreakingLevel)

            if (random() < 0.5) weapon.addEnchantment(Enchantment.FIRE_ASPECT, 1)

            entity.equipment?.setItemInMainHand(weapon)
        }

        private fun giveEnchantedBow(entity: LivingEntity) {
            val bow = ItemStack(Material.BOW)

            val powerLevel = (2..4).random()
            val unbreakingLevel = (1..3).random()

            bow.addEnchantment(Enchantment.ARROW_DAMAGE, powerLevel)
            bow.addEnchantment(Enchantment.DURABILITY, unbreakingLevel)

            if (random() < 0.5) bow.addEnchantment(Enchantment.ARROW_FIRE, 1)

            entity.equipment?.setItemInMainHand(bow)
        }
    }
}