package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.getCoreProtect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

val validAxes = listOf(
    Material.WOODEN_AXE,
    Material.STONE_AXE,
    Material.IRON_AXE,
    Material.GOLDEN_AXE,
    Material.DIAMOND_AXE,
    Material.NETHERITE_AXE,
)

val validLogs = listOf(
    Material.ACACIA_LOG,
    Material.ACACIA_WOOD,
    Material.BIRCH_LOG,
    Material.BIRCH_WOOD,
    Material.DARK_OAK_LOG,
    Material.DARK_OAK_WOOD,
    Material.JUNGLE_LOG,
    Material.JUNGLE_WOOD,
    Material.OAK_LOG,
    Material.OAK_WOOD,
    Material.SPRUCE_LOG,
    Material.SPRUCE_WOOD,
    Material.CRIMSON_STEM,
    Material.CRIMSON_HYPHAE,
    Material.WARPED_STEM,
    Material.WARPED_HYPHAE,
)

class TimberEnchant : RedEnchant(
    name = "timber",
    maxLevel = 3,
    itemTarget = EnchantmentTarget.TOOL,
    canEnchant = { i -> validAxes.contains(i.type) },
), Listener {
    private val coreProtect = getCoreProtect()!!
    private val config = RedEnchants.instance.config.data.timber

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!validLogs.contains(e.block.type)) return

        if (coreProtect.blockLookup(e.block, (System.currentTimeMillis() / 1000L).toInt()).size > 0) return

        val player = e.player
        val item = player.inventory.itemInMainHand
        val hasTelepathy = item.enchantments.containsKey(CustomEnchants.TELEPATHY)

        if (!item.itemMeta.hasEnchant(this) || player.gameMode != GameMode.SURVIVAL) return

        val numToBreak = item.itemMeta.getEnchantLevel(this) * config.amountPerLevel

        val adjacentLogs = findAdjacentLogs(e.block).take(numToBreak)

        object : BukkitRunnable() {
            val iterator = adjacentLogs.iterator()

            override fun run() {
                if (iterator.hasNext()) {
                    val block = iterator.next()

                    if (hasTelepathy) {
                        player.inventory.addItem(ItemStack(block.type))
                        player.playSound(block.location, Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.0f)
                        block.type = Material.AIR
                    } else {
                        block.breakNaturally(item)
                    }

                    player.playSound(block.location, Sound.BLOCK_WOOD_BREAK, 0.3f, 1.0f)
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(RedEnchants.instance, 0L, 5L)
    }

    private fun findAdjacentLogs(block: Block, visited: MutableSet<Block> = mutableSetOf()): List<Block> {
        val playerPlaced = coreProtect.blockLookup(block, (System.currentTimeMillis() / 1000L).toInt()).size > 0
        val adjacentLogs = mutableListOf<Block>()

        fun exploreAdjacent(b: Block) {
            visited.add(b)

            for (horizontal in -1..1) {
                for (vertical in -1..1) {
                    for (depth in -1..1) {
                        if (horizontal == 0 && vertical == 0 && depth == 0) continue

                        val relativeBlock = b.getRelative(horizontal, vertical, depth)

                        if (!visited.contains(relativeBlock) && validLogs.contains(relativeBlock.type) && !playerPlaced) {
                            adjacentLogs.add(relativeBlock)
                            exploreAdjacent(relativeBlock)
                        }
                    }
                }
            }
        }

        exploreAdjacent(block)

        return adjacentLogs
    }
}
