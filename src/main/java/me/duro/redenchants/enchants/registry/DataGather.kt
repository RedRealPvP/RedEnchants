package me.duro.redenchants.enchants.registry

import me.duro.redenchants.enchants.types.EnchantType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack


abstract class DataGather<E : Event?, T : EnchantType?> {
    abstract fun getEntity(event: E?): LivingEntity?
    abstract fun getEnchantSlots(event: E): Array<EquipmentSlot>
    abstract fun checkPriority(enchant: T, priority: EventPriority): Boolean
    abstract fun useEnchant(event: E, entity: LivingEntity, item: ItemStack, enchant: T, level: Int): Boolean

    fun getEnchants(event: E, enchantClass: Class<T>, entity: LivingEntity): Map<ItemStack, Map<T, Int>> {
        return EnchantUtils.getEquipped(entity, enchantClass, *getEnchantSlots(event))
    }
}