package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface Periodic {
    val periodImplementation: Periodic
    var interval: Long
        get() = periodImplementation.interval
        set(value) {
            periodImplementation.interval = value
        }

    val nextTriggerTime: Long
        get() = periodImplementation.nextTriggerTime

    val isTriggerTime: Boolean
        get() = periodImplementation.isTriggerTime

    fun updateTriggerTime() {
        periodImplementation.updateTriggerTime()
    }
}

class PeriodicImpl(override val periodImplementation: Periodic, periodInterval: Long = 5) : Periodic {
    override var interval: Long = periodInterval * 1000
    override var nextTriggerTime: Long = System.currentTimeMillis() + interval
    override val isTriggerTime: Boolean
        get() = System.currentTimeMillis() >= nextTriggerTime

    override fun updateTriggerTime() {
        nextTriggerTime = System.currentTimeMillis() + interval
    }
}

interface PassiveEnchant : EnchantType, Periodic {
    fun onTrigger(entity: LivingEntity, item: ItemStack, level: Int): Boolean
}
