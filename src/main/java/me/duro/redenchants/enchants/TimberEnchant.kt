package me.duro.redenchants.enchants

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.registry.EnchantUtils
import me.duro.redenchants.enchants.registry.EnchantUtils.safeBusyBreak
import me.duro.redenchants.enchants.types.BlockBreakEnchant
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

private val config = RedEnchants.instance.config.data.timber
private fun numToBreak(level: Int) = config.amountPerLevel * level

class TimberEnchant : RedEnchant(
    name = "timber",
    description = { l -> "Breaks ${numToBreak(l)} connected logs." },
    canEnchant = { i -> RedEnchantTarget.AXE.match(i) },
    maxLevel = 3,
    enchantRarity = RedEnchantRarity.RARE,
), BlockBreakEnchant {
    override fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (player !is Player || !Tag.LOGS.values.contains(event.block.type) || player.isSneaking || EnchantUtils.isBusy) return false

        val block = event.block

        if (block.getDrops(item).isEmpty()) return false
        if (!Tag.LOGS.values.contains(block.type)) return false

        timberMine(player, event.block, item, level)
        return true
    }

    private fun timberMine(player: Player, block: Block, item: ItemStack, level: Int = 1) {
        val logs = mutableSetOf<Block>()
        val prepare = mutableSetOf<Block>().apply { addAll(findAdjacentLogs(block)) }
        val durability = item.type.maxDurability - (item.itemMeta as Damageable).damage

        val num = if (durability >= numToBreak(level)) numToBreak(level) else durability

        while (logs.addAll(prepare) && logs.size < num) {
            val nearby = mutableSetOf<Block>()
            prepare.forEach { nearby.addAll(findAdjacentLogs(it)) }
            prepare.clear()
            prepare.addAll(nearby)
        }

        logs.remove(block)

        logs.take(numToBreak(level) - 1).forEach {
            safeBusyBreak(player, it)
        }
    }

    private fun findAdjacentLogs(block: Block, visited: MutableSet<Block> = mutableSetOf()): Set<Block> {
        val adjacentLogs = mutableSetOf<Block>()

        fun exploreAdjacent(b: Block) {
            visited.add(b)

            for (horizontal in -1..1) {
                for (vertical in -1..1) {
                    for (depth in -1..1) {
                        if (horizontal == 0 && vertical == 0 && depth == 0) continue

                        val relativeBlock = b.getRelative(horizontal, vertical, depth)

                        if (!visited.contains(relativeBlock) && Tag.LOGS.values.contains(relativeBlock.type)) {
                            adjacentLogs.add(relativeBlock)
                            exploreAdjacent(relativeBlock)
                        }
                    }
                }
            }
        }

        exploreAdjacent(block)

        return adjacentLogs.apply {
            addAll(BlockFace.entries.map { block.getRelative(it) }.filter { Tag.LOGS.values.contains(it.type) })
        }
    }
}
