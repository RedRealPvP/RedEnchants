package me.duro.redenchants.enchants

import me.duro.redenchants.enchants.registry.EnchantUtils
import me.duro.redenchants.enchants.registry.EnchantUtils.safeBusyBreak
import me.duro.redenchants.enchants.types.BlockBreakEnchant
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

private val validOres = mutableSetOf<Material>().apply {
    addAll(Tag.COAL_ORES.values)
    addAll(Tag.COPPER_ORES.values)
    addAll(Tag.IRON_ORES.values)
    addAll(Tag.GOLD_ORES.values)
    addAll(Tag.REDSTONE_ORES.values)
    addAll(Tag.LAPIS_ORES.values)
    addAll(Tag.DIAMOND_ORES.values)
    addAll(Tag.EMERALD_ORES.values)
    add(Material.NETHER_GOLD_ORE)
    add(Material.NETHER_QUARTZ_ORE)
    add(Material.ANCIENT_DEBRIS)
}

class VeinMinerEnchant : RedEnchant(
    name = "vein-miner",
    description = { _ -> "Mines a vein of ores." },
    canEnchant = { i -> RedEnchantTarget.PICKAXE.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS
), BlockBreakEnchant {
    override fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (player !is Player || player.isSneaking || EnchantUtils.isBusy) return false

        val block = event.block

        if (block.getDrops(item).isEmpty()) return false
        if (!validOres.contains(block.type)) return false

        veinMine(player, block)
        return true
    }

    private fun getNearby(block: Block): List<Block> {
        return BlockFace.entries.map { block.getRelative(it) }.filter { validOres.contains(it.type) }
    }

    private fun veinMine(player: Player, block: Block) {
        val ores = mutableSetOf<Block>()
        val prepare = mutableSetOf<Block>().apply { addAll(getNearby(block)) }

        while (ores.addAll(prepare) && ores.size < 100) {
            val nearby = mutableSetOf<Block>()
            prepare.forEach { nearby.addAll(getNearby(it)) }
            prepare.clear()
            prepare.addAll(nearby)
        }

        ores.remove(block)

        ores.forEach { safeBusyBreak(player, it) }
    }
}