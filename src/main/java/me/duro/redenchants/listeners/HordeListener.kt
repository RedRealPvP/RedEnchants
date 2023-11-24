package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
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

        if (isHordeMob(entity)) e.drops.add(soulItem) // TODO: add telepathy enchantment check
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        val item = e.currentItem ?: return

        if (item.type == Material.NETHER_STAR && item.itemMeta?.customModelData == 1) {
            e.isCancelled = true
        }
    }

    companion object {
        val hordeKey = NamespacedKey(RedEnchants.instance, "isHorde")
        val hordeMobTypes = listOf(
            EntityType.ZOMBIE,
            EntityType.HUSK,
            EntityType.SKELETON,
            EntityType.STRAY,
        )

        val soulItem = ItemStack(Material.NETHER_STAR).apply {
            itemMeta = itemMeta.apply {
                setCustomModelData(1) // NOTE: This stops the item from being able to be used in crafting recipes
                displayName(replaceColorCodes("&cHorde Soul"))

                lore(
                    listOf(
                        replaceColorCodes("&7A soul of a horde mob."),
                        replaceColorCodes("&7Can be bartered with the &cHorde Trader&7.")
                    )
                )
            }
        }

        fun isHordeMob(entity: Entity): Boolean {
            return hordeMobTypes.contains(entity.type) && entity.persistentDataContainer.getOrDefault(
                hordeKey, PersistentDataType.BYTE, 0.toByte()
            ) == 1.toByte()
        }

        fun setHordeMob(entity: Entity) {
            entity.persistentDataContainer.set(hordeKey, PersistentDataType.BYTE, 1.toByte())
        }
    }
}