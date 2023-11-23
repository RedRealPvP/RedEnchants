package me.duro.redenchants.commands

import me.duro.redenchants.enchants.CustomEnchants
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class RedEnchantsCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if (args?.size == 0) {
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <enchantment> [level]"))
            return true
        }

        val enchant = CustomEnchants.allEnchants.find { it.key.key == args?.get(0) } ?: return true
        val item = sender.inventory.itemInMainHand
        val level = if (args?.size == 2) args[1].toInt() else 1

        if (item.type == Material.AIR) {
            sender.sendMessage(replaceColorCodes("&cYou must be holding an item."))
            return true
        }

        if (level > enchant.maxLevel) {
            sender.sendMessage(replaceColorCodes("&cThe max level for this enchantment is ${enchant.maxLevel}."))
            return true
        }

        if (!enchant.canEnchantItem(item)) {
            sender.sendMessage(replaceColorCodes("&cThis item cannot be enchanted with ${enchant.key.key}."))
            return true
        }

        if (item.enchantments.containsKey(enchant)) {
            sender.sendMessage(replaceColorCodes("&cThis item already has ${enchant.key.key}."))
            return true
        }

        if (item.enchantments.any { enchant.conflictsWith(it.key) }) {
            sender.sendMessage(replaceColorCodes("&cThis item conflicts with another enchantment."))
            return true
        }

        item.addUnsafeEnchantment(enchant, level)

        if (item.lore() != null) {
            println("item has lore")
            item.lore()!!.add(enchant.displayName(level))
        } else {
            item.lore(listOf(enchant.displayName(level)))
        }

        sender.sendMessage(replaceColorCodes("&aSuccessfully added ${enchant.key.key} to your item."))

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return CustomEnchants.allEnchants.map { it.key.key }.filter { it.startsWith(args[0]) }
        }

        if (args.size == 2) {
            val maxLvl = CustomEnchants.allEnchants.find { it.key.key == args[0] }?.maxLevel ?: return emptyList()
            return (1..maxLvl).map { it.toString() }.filter { it.startsWith(args[1]) }
        }

        return emptyList()
    }
}