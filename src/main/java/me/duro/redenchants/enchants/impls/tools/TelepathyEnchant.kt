package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.registry.RedEnchant
import me.duro.redenchants.enchants.registry.RedEnchantRarity
import me.duro.redenchants.enchants.registry.RedEnchantTarget
import me.duro.redenchants.enchants.types.BlockDropEnchant
import me.duro.redenchants.enchants.types.DeathEnchant
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class TelepathyEnchant : RedEnchant(
    name = "telepathy",
    description = { _ -> "Moves all blocks loot directly to your inventory." },
    canEnchant = { i -> RedEnchantTarget.TOOL.match(i) || RedEnchantTarget.SWORD.match(i) },
    enchantRarity = RedEnchantRarity.COMMON,
), BlockDropEnchant, DeathEnchant {
    private val config = RedEnchants.instance.config.data

    override fun onDrop(event: BlockDropItemEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (player !is Player || player.gameMode != GameMode.SURVIVAL || player.inventory.firstEmpty() == -1 || event.block.state is Container || event.items.isEmpty()) return false

        event.items.forEach { player.inventory.addItem(it.itemStack) }
        event.items.clear()

        player.playSound(event.block.location, Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.0f)
        return true
    }

    override fun onKill(
        event: EntityDeathEvent, entity: LivingEntity, killer: Player, weapon: ItemStack?, level: Int
    ): Boolean {
        if (entity !is Player) return false

        placeChest(entity)
        event.drops.clear()

        killer.sendMessage(
            replaceColorCodes(
                config.telepathy.message.replace("{x}", entity.location.blockX.toString())
                    .replace("{y}", entity.location.blockY.toString()).replace("{z}", entity.location.blockZ.toString())
                    .replace("{m}", config.telepathy.chestDuration.toString())
            )
        )

        return true
    }

    private fun placeChest(player: Player) {
        val location = suitableChestLocation(player.location)

        if (location == null) {
            player.inventory.forEach {
                player.world.dropItem(
                    player.location, it
                )
            }

            return
        }

        val leftChest = location.block
        leftChest.type = Material.CHEST
        leftChest.blockData = Material.CHEST.createBlockData("[type=right]")

        val rightChest = leftChest.getRelative(BlockFace.EAST)
        rightChest.type = Material.CHEST
        leftChest.blockData = Material.CHEST.createBlockData("[type=left]")

        val leftChestInventory = leftChest.state as Chest
        val rightChestInventory = rightChest.state as Chest

        for (i in 0 until player.inventory.size) {
            val item = player.inventory.getItem(i)
            if (item == null || item.type.isAir) continue

            if (leftChestInventory.inventory.firstEmpty() != -1) {
                leftChestInventory.inventory.setItem(i, item.clone())
            } else if (rightChestInventory.inventory.firstEmpty() != -1) {
                rightChestInventory.inventory.setItem(i, item.clone())
            }
        }

        player.inventory.clear()

        RedEnchants.instance.server.scheduler.runTaskLater(RedEnchants.instance, Runnable {
            leftChest.breakNaturally()
            rightChest.breakNaturally()
        }, config.telepathy.chestDuration * 60 * 20L)
    }

    private fun suitableChestLocation(location: Location): Location? {
        for (radius in 1..3) {
            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val block =
                            location.world.getBlockAt(location.blockX + x, location.blockY + y, location.blockZ + z)

                        if (block.type.isAir && block.getRelative(BlockFace.EAST).type.isAir) return block.location
                    }
                }
            }
        }

        return null
    }

    override fun onDeath(event: EntityDeathEvent, entity: LivingEntity, item: ItemStack?, level: Int) = false
}
