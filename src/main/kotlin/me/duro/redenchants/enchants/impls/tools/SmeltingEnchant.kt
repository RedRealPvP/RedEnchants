package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.BlockBreakEnchant
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack

class SmeltingEnchant : RedEnchant(
    name = "smelting",
    description = { _ -> "Smelts ores upon break." },
    canEnchant = { i -> RedEnchantTarget.PICKAXE.match(i) },
    enchantRarity = RedEnchantRarity.FABULOUS
), BlockBreakEnchant {
    override fun onBreak(event: BlockBreakEvent, player: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (player !is Player || player.isSneaking) return false

        val block = event.block
        val drops = block.getDrops(item)

        if (drops.isEmpty()) return false

        val smelted = drops.map { smeltItem(it) }

        event.isDropItems = false

        smelted.forEach { block.world.dropItemNaturally(block.location, it) }

        return true
    }

    private fun smeltItem(item: ItemStack): ItemStack {
        val recipes = Bukkit.recipeIterator()
        var result: ItemStack? = null

        while (recipes.hasNext()) {
            val recipe = recipes.next()

            if (recipe !is FurnaceRecipe) continue

            if (recipe.input.type == item.type) {
                result = recipe.result
                break
            }
        }

        return result ?: item
    }
}