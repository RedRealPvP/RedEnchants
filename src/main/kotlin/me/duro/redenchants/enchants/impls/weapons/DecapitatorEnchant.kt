package me.duro.redenchants.enchants.impls.weapons

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.DeathEnchant
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

private val config = RedEnchants.instance.config.data.decapitator

class DecapitatorEnchant : RedEnchant(
    name = "decapitator",
    description = { _ -> "Drops player heads upon killing them." },
    canEnchant = { i -> RedEnchantTarget.AXE.match(i) },
    enchantRarity = RedEnchantRarity.EXOTIC,
), DeathEnchant {
    override fun onDeath(event: EntityDeathEvent, entity: LivingEntity, item: ItemStack?, level: Int) = false

    override fun onKill(
        event: EntityDeathEvent, entity: LivingEntity, killer: Player, weapon: ItemStack?, level: Int
    ): Boolean {
        if (entity !is Player) return false

        val skull = ItemStack(Material.PLAYER_HEAD)
        val price = (RedEnchants.instance.vault.balance(entity) * config.balancePercent).toInt()

        val meta = (skull.itemMeta as SkullMeta).apply {
            owningPlayer = entity
            persistentDataContainer.set(
                NamespacedKey(RedEnchants.instance, "decapitator_price"), PersistentDataType.INTEGER, price
            )
            displayName(replaceColorCodes("&c${entity.name}'s Head"))
            lore(listOf(replaceColorCodes("&7Sell at the &cHead Hunter &7NPC to receive &c$${price}&7.")))
        }

        skull.itemMeta = meta

        entity.world.dropItemNaturally(entity.location, skull)

        entity.eyeLocation.world.spawnParticle(
            Particle.BLOCK_CRACK, entity.eyeLocation, 100, 0.0, 0.0, 0.0, 0.0, Material.REDSTONE_BLOCK.createBlockData()
        )

        return true
    }

    override val killPriority = EventPriority.LOWEST
}