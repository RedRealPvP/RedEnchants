package me.duro.redenchants.enchants.impls.tools

import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.RedEnchantTarget
import me.duro.redenchants.enchants.types.InteractEnchant
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.entity.LivingEntity
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

private val validCrops =
    listOf(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART)

class HarvestEnchant : RedEnchant(
    name = "harvest",
    description = { "Right click to harvest and replant crops." },
    canEnchant = { i -> RedEnchantTarget.HOE.match(i) },
    enchantRarity = RedEnchantRarity.COMMON,
), InteractEnchant {
    override fun onInteract(event: PlayerInteractEvent, entity: LivingEntity, item: ItemStack, level: Int): Boolean {
        if (event.hand != EquipmentSlot.HAND || event.action != Action.RIGHT_CLICK_BLOCK) return false

        val block = event.clickedBlock ?: return false
        if (!validCrops.contains(block.type)) return false

        val cropData = block.blockData as? Ageable ?: return false
        if (cropData.age != cropData.maximumAge) return false

        val drops = block.getDrops(item)
        drops.forEach { block.world.dropItemNaturally(block.location, it) }

        cropData.age = 0
        block.blockData = cropData

        return true
    }
}
