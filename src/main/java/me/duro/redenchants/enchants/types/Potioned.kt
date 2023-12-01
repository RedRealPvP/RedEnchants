package me.duro.redenchants.enchants.types

import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

interface Potioned {
    val isPermanent: Boolean
        get() = false
    val effectType: PotionEffectType?
        get() = null

    fun getEffectAmplifier(level: Int): Int {
        return level - 1
    }

    fun getEffectDuration(level: Int): Int {
        return 0
    }

    fun createEffect(level: Int): PotionEffect? {
        val duration = if (isPermanent) Int.MAX_VALUE else getEffectDuration(level)
        return effectType?.let { PotionEffect(it, duration, getEffectAmplifier(level) ) }
    }

    fun addEffect(target: LivingEntity, level: Int): Boolean {
        return createEffect(level)?.let { target.addPotionEffect(it) } ?: false
    }
}

