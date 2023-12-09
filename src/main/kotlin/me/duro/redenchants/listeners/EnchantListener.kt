package me.duro.redenchants.listeners

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.enchants.registry.CustomEnchants.addEnchant
import me.duro.redenchants.enchants.registry.CustomEnchants.generateEnchantBook
import me.duro.redenchants.enchants.registry.CustomEnchants.randomEnchantBook
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.*
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.random.Random

class EnchantListener : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = player.inventory.itemInMainHand

        if (!item.hasItemMeta() || !item.itemMeta.hasLore() || !item.itemMeta.hasDisplayName()) return

        val itemName = componentToString(item.itemMeta.displayName()!!)

        if (itemName.contains("Random") && itemName.contains("Enchant")) {
            val enchantBookNames = RedEnchantRarity.entries.map {
                componentToString(randomEnchantBook(it).itemMeta.displayName()!!)
            }

            val matchedBook = enchantBookNames.firstOrNull { itemName == it } ?: return

            val rarity = RedEnchantRarity.entries.firstOrNull {
                " $matchedBook".lowercase().contains(" ${it.name.lowercase()}")
            } ?: return

            val enchantsWithRarity = CustomEnchants.allEnchants.filter { it.enchantRarity == rarity }

            if (enchantsWithRarity.isEmpty()) return player.sendMessage(replaceColorCodes("&cThere are no enchantments with this rarity!"))

            val enchant = enchantsWithRarity.random()
            val level = Random.nextInt(enchant.maxLevel) + 1

            val enchantBook = generateEnchantBook(enchant, level)

            player.location.world.spawnParticle(
                Particle.FIREWORKS_SPARK, player.location, 50, 0.5, 1.0, 0.5, 0.0
            )

            player.location.world.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f)

            player.sendMessage(replaceColorCodes("&7You have received a ${componentToString(enchant.displayName(level))}&7 enchantment book!"))

            player.inventory.setItem(player.inventory.heldItemSlot, enchantBook)
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as Player

        val cursorItem = e.cursor ?: return
        val currentItem = e.currentItem ?: return

        if (!cursorItem.hasItemMeta() || !cursorItem.itemMeta.hasLore() || !cursorItem.itemMeta.hasDisplayName()) return

        val enchant = cursorItem.itemMeta.persistentDataContainer.get(
            NamespacedKey(RedEnchants.instance, "red_enchant"), PersistentDataType.STRING
        )?.let { CustomEnchants.allEnchants.find { e -> e.key.key == it } } ?: return

        val level = cursorItem.itemMeta.persistentDataContainer.get(
            NamespacedKey(RedEnchants.instance, "red_enchant_level"), PersistentDataType.INTEGER
        ) ?: return

        val currentLvl = currentItem.getEnchantmentLevel(enchant)
        val containsConflict =
            if (currentItem.hasItemMeta() && currentItem.itemMeta.hasEnchants()) currentItem.itemMeta.enchants.keys.any {
                enchant.conflictsWith(it)
            }
            else false

        if (!enchant.canEnchantItem(currentItem) || currentLvl >= enchant.maxLevel || containsConflict) return

        e.isCancelled = true

        if (currentLvl > 0) {
            currentItem.removeEnchantment(enchant)

            val currentLore = currentItem.itemMeta.lore()!!.toMutableList()
            currentLore.remove(enchant.displayName(currentLvl))

            val meta = currentItem.itemMeta
            meta.lore(currentLore)

            currentItem.itemMeta = meta
        }

        addEnchant(currentItem, enchant, level)
        cursorItem.subtract(1)

        player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f)
    }
}