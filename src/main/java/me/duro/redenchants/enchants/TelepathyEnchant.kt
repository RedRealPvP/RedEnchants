package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.Config
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class TelepathyEnchant : RedEnchant(
    name = "telepathy",
    itemTarget = EnchantmentTarget.BREAKABLE,
    canEnchant = { i -> EnchantmentTarget.TOOL.includes(i) || EnchantmentTarget.WEAPON.includes(i) },
), Listener {
    private val config = Config.load().data

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (e.entity.killer !is Player || e.entity !is Player) return

        val player = e.entity.killer as Player
        val victim = e.entity as Player
        val item = player.inventory.itemInMainHand

        if (!item.enchantments.containsKey(this)) return

        if (item.enchantments.containsKey(CustomEnchants.DECAPITATOR)) {
            if (Math.random() < config.decapitator.dropChance) {
                val skull = ItemStack(Material.PLAYER_HEAD)
                val meta = (skull.itemMeta as SkullMeta).apply { owningPlayer = victim }
                skull.itemMeta = meta

                victim.inventory.addItem(skull)
            }
        }

        placeChest(victim)
        e.drops.clear()

        player.sendMessage(
            replaceColorCodes(
                config.telepathy.message
                    .replace("{x}", victim.location.blockX.toString())
                    .replace("{y}", victim.location.blockY.toString())
                    .replace("{z}", victim.location.blockZ.toString())
                    .replace("{m}", config.telepathy.chestDuration.toString())
            )
        )
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val item = player.inventory.itemInMainHand

        if (
            !item.enchantments.containsKey(this)
            || player.gameMode != GameMode.SURVIVAL
            || player.inventory.firstEmpty() == -1
            || e.block.state is Container
        ) return

        e.isDropItems = false

        val drops = e.block.getDrops(item)
        if (drops.isEmpty()) return

        drops.forEach { player.inventory.addItem(it) }
        player.playSound(e.block.location, Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.0f)
    }

    private fun placeChest(player: Player) {
        val location = suitableChestLocation(player.location)

        if (location == null) {
            player.inventory.forEach {
                player.world.dropItem(
                    player.location,
                    it
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
}
