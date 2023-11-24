package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class HordeListener : Listener {
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

            player.sendMessage(replaceColorCodes("&cYou defeated a horde mob and gained 1 soul."))
            player.sendMessage(replaceColorCodes("&cSouls: ${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}"))
        }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        val item = e.currentItem ?: return

        if (item.type == Material.NETHER_STAR && item.itemMeta?.customModelData == 1) {
            e.isCancelled = true
        }
    }

    companion object {
        private val hordeKey = NamespacedKey(RedEnchants.instance, "isHorde")
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
                val x = (location.x + (Math.random() * hordeRadius * 2) - hordeRadius).toInt()
                val z = (location.z + (Math.random() * hordeRadius * 2) - hordeRadius).toInt()
                val y = location.world.getHighestBlockYAt(x, z)

                val hordeMembers = listOf("Guard", "Soldier", "Warrior", "Assassin", "Knight")

                spawnHordeMob(
                    Location(location.world, x.toDouble(), y.toDouble() + 2, z.toDouble()),
                    if (i == 0) "&4&lHorde Leader" else "&c&lHorde ${hordeMembers.random()}",
                    i == 0
                )
            }
        }

        private fun spawnHordeMob(location: Location, name: String = "&c&lHorde Guard", isLeader: Boolean = false) {
            val mobType = hordeMobTypes.random()
            val mob = location.world.spawnEntity(location, mobType) as LivingEntity

            mob.customName(replaceColorCodes(name))
            mob.removeWhenFarAway = false

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
            center: Location,
            radius: Double,
            amount: Int,
            maxSpawnHeight: Int = 170
        ): List<Location> {
            val locations = mutableListOf<Location>()

            for (i in 0 until amount) {
                var x: Double
                var z: Double
                var y: Double

                do {
                    x = center.x + Math.random() * radius * 2 - radius
                    z = center.z + Math.random() * radius * 2 - radius
                    y = center.world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble()
                } while (y > maxSpawnHeight)

                locations.add(Location(center.world, x, y, z))
            }

            return locations
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

            if (Math.random() < 0.5) weapon.addEnchantment(Enchantment.FIRE_ASPECT, 1)

            entity.equipment?.setItemInMainHand(weapon)
        }

        private fun giveEnchantedBow(entity: LivingEntity) {
            val bow = ItemStack(Material.BOW)

            val powerLevel = (2..4).random()
            val unbreakingLevel = (1..3).random()

            bow.addEnchantment(Enchantment.ARROW_DAMAGE, powerLevel)
            bow.addEnchantment(Enchantment.DURABILITY, unbreakingLevel)

            if (Math.random() < 0.5) bow.addEnchantment(Enchantment.ARROW_FIRE, 1)

            entity.equipment?.setItemInMainHand(bow)
        }
    }
}