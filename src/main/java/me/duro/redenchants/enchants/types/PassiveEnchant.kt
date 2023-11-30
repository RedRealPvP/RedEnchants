package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface Periodic {
    val periodImplementation: Periodic
    val interval: Long
        get() = periodImplementation.interval

    val nextTriggerTime: Long
        get() = periodImplementation.nextTriggerTime

    val isTriggerTime: Boolean
        get() = periodImplementation.isTriggerTime

    fun updateTriggerTime() {
        periodImplementation.updateTriggerTime()
    }
}


interface PassiveEnchant : EnchantType {
    fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean
}