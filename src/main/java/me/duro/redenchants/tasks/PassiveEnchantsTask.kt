package me.duro.redenchants.tasks

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.enchants.registry.EnchantUtils
import me.duro.redenchants.enchants.types.PassiveEnchant
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

class PassiveEnchantsTask : AbstractTask() {
    private val passiveEnchants = CustomEnchants.allEnchants.filterIsInstance<PassiveEnchant>().map {
        Pair(it, it as RedEnchant)
    }
    override val interval: Long = 20L
    override val async: Boolean = false

    override fun action() {
        if (passiveEnchants.isEmpty()) return

        val entities = getEntities()

        passiveEnchants.forEach {
            if (!it.first.isTriggerTime) return@forEach

            entities.forEach { entity ->
                EnchantUtils.getEquipped(
                    entity,
                    it.first.javaClass,
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
                ).forEach { (item, level) ->
                    if (it.second.canEnchant(item)) it.first.onTrigger(entity, item, level[it.first] ?: 0)
                }
            }

            it.first.updateTriggerTime()
        }
    }

    private fun getEntities(): Set<Player> {
        val set = mutableSetOf<Player>().apply { addAll(RedEnchants.instance.server.onlinePlayers) }

        set.removeIf { it.isDead || it.health <= 0.0 || !it.isValid }

        return set
    }

}