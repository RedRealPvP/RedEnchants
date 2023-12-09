package me.duro.redenchants.npcs

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.replaceColorCodes
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType

class HeadHunter : Trait("head_hunter") {
    @EventHandler
    fun onNPCRightClick(e: NPCRightClickEvent) {
        if (!e.npc.hasTrait(HeadHunter::class.java)) return

        val player = e.clicker
        val item = player.inventory.itemInMainHand

        if (item.type != Material.PLAYER_HEAD || !item.hasItemMeta()) return player.sendMessage(
            replaceColorCodes("&cYou must be holding a valid decapitator head to use this!")
        )

        val price = item.itemMeta.persistentDataContainer.get(
            NamespacedKey(RedEnchants.instance, "decapitator_price"), PersistentDataType.INTEGER
        )

        val skullOwner = (item.itemMeta as SkullMeta).owningPlayer!!

        if (price != null) {
            RedEnchants.instance.vault.deposit(player, price.toDouble())
            RedEnchants.instance.vault.withdraw(skullOwner, price.toDouble())

            player.inventory.remove(item)
            player.sendMessage(replaceColorCodes("&7You have sold &c${skullOwner.name}&7's head for &c$$price&7!"))
        } else player.sendMessage(replaceColorCodes("&cYou must be holding a valid decapitator head to use this!"))
    }
}