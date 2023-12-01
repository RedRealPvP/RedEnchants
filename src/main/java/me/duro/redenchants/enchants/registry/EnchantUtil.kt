package me.duro.redenchants.enchants.registry

import me.duro.redenchants.enchants.types.EnchantType
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

object EnchantUtils {
    var isBusy = false

    private fun busyBreak(player: Player, block: Block) {
        isBusy = true
        player.breakBlock(block)
        isBusy = false
    }

    fun safeBusyBreak(player: Player, block: Block) {
        if (!isBusy) busyBreak(player, block)
    }

    private fun getAll(item: ItemStack): Map<Enchantment, Int> {
        val meta = item.itemMeta
        return if (meta == null) emptyMap() else getAll(meta)
    }

    private fun getAll(meta: ItemMeta): Map<Enchantment, Int> {
        return (meta as? EnchantmentStorageMeta)?.storedEnchants ?: meta.enchants
    }

    private fun <T : EnchantType?> getExcellents(item: ItemStack, clazz: Class<T>): Map<T, Int> {
        val map: MutableMap<T, Int> = HashMap()
        getAll(item).forEach { (enchantment, level) ->
            val enchant = CustomEnchants.allEnchants.find { it.key == enchantment.key }
            if (enchant == null || !clazz.isAssignableFrom(enchant::class.java)) return@forEach
            map[clazz.cast(enchant)] = level
        }

        return map
    }

    private fun getEnchantedEquipment(
        entity: LivingEntity, vararg slots: EquipmentSlot
    ): Map<EquipmentSlot, ItemStack> {
        val equipment = mutableMapOf<EquipmentSlot, ItemStack>()
        val entityEquipment = entity.equipment ?: return equipment

        for (slot in slots) {
            equipment[slot] = entityEquipment.getItem(slot)
        }

        equipment.entries.removeIf { (slot, item): Map.Entry<EquipmentSlot, ItemStack> ->
            if (item.type.isAir || item.type == Material.ENCHANTED_BOOK) return@removeIf true
            val armorSlots = arrayOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
            if ((slot === EquipmentSlot.HAND || slot === EquipmentSlot.OFF_HAND) && armorSlots.contains(slot)) return@removeIf true
            !item.hasItemMeta()
        }

        return equipment
    }

    fun <T : EnchantType?> getEquipped(
        entity: LivingEntity, clazz: Class<T>, vararg slots: EquipmentSlot
    ): Map<ItemStack, MutableMap<T, Int>> {
        val map: MutableMap<ItemStack, MutableMap<T, Int>> = HashMap()
        val equipment = getEnchantedEquipment(entity, *slots)

        equipment.forEach { (_, item) ->
            val enchants = getExcellents(item, clazz)
            if (enchants.isEmpty()) return@forEach
            map[item] = enchants as MutableMap<T, Int>
        }

        return map
    }
}