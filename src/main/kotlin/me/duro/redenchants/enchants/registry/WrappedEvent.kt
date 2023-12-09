package me.duro.redenchants.enchants.registry

import me.duro.redenchants.enchants.types.EnchantType
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

class WrappedEvent<E : Event?, T : EnchantType?>(
    private val priority: EventPriority,
    private val eventClass: Class<E>,
    private val enchantClass: Class<T>,
    private val dataGather: DataGather<E, T>
) : Listener, EventExecutor {
    override fun execute(listener: Listener, bukkitEvent: Event) {
        if (!eventClass.isAssignableFrom(bukkitEvent.javaClass)) return

        val event = eventClass.cast(bukkitEvent)
        val entity = dataGather.getEntity(event) ?: return

        dataGather.getEnchants(event, enchantClass, entity).forEach { (item, enchants) ->
            enchants.forEach label@{ enchant, level ->
                if (!dataGather.checkPriority(enchant, priority)) return@label
                this.dataGather.useEnchant(event, entity, item, enchant, level)
            }
        }
    }
}