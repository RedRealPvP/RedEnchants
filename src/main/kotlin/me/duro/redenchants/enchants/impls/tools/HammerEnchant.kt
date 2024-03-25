package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.registry.EnchantUtils
import me.duro.redenchants.enchants.types.BlockBreakEnchant
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

private val interactableBlocks = setOf(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE)
private val coordOffsets = arrayOf(
    intArrayOf(0, 0),
    intArrayOf(0, -1),
    intArrayOf(-1, 0),
    intArrayOf(0, 1),
    intArrayOf(1, 0),
    intArrayOf(-1, -1),
    intArrayOf(-1, 1),
    intArrayOf(1, -1),
    intArrayOf(1, 1)
)

class HammerEnchant : RedEnchant(
    name = "hammer",
    description = { "Breaks a 3x3 of blocks." },
    canEnchant = { i -> RedEnchantTarget.TOOL.match(i) },
    enchantRarity = RedEnchantRarity.EXOTIC,
), BlockBreakEnchant {
    override fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (player !is Player || player.isSneaking || EnchantUtils.isBusy) return false

        val block = event.block

        if (block.type.isInteractable && !interactableBlocks.contains(block.type)) return false
        if (block.getDrops(item).isEmpty()) return false

        val lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 5)
        if (lastTwoTargetBlocks.size < 2 || !lastTwoTargetBlocks[1].type.isOccluding) return false

        val adjacentBlock = lastTwoTargetBlocks[0]
        val targetBlock = lastTwoTargetBlocks[1]
        val dir = targetBlock.getFace(adjacentBlock)
        val isZ = dir == BlockFace.EAST || dir == BlockFace.WEST

        (0 until 9).forEach {
            if (item.type.isAir) return@forEach

            val xAdd = coordOffsets[it][0]
            val zAdd = coordOffsets[it][1]

            val blockAdd = if (dir === BlockFace.UP || dir === BlockFace.DOWN) {
                block.location.clone().add(xAdd.toDouble(), 0.0, zAdd.toDouble()).block
            } else {
                block.location.clone().add(
                    (if (isZ) 0 else xAdd).toDouble(), zAdd.toDouble(), (if (isZ) xAdd else 0).toDouble()
                ).block
            }

            if (blockAdd == block || blockAdd.getDrops(item).isEmpty() || blockAdd.isLiquid) return@forEach

            val addType = blockAdd.type

            if (addType.isInteractable && !interactableBlocks.contains(addType)) return@forEach
            if (addType == Material.BEDROCK || addType == Material.END_PORTAL || addType == Material.END_PORTAL_FRAME) return@forEach
            if (addType == Material.OBSIDIAN && addType != block.type) return@forEach

            EnchantUtils.safeBusyBreak(player, blockAdd)
        }

        return true
    }
}