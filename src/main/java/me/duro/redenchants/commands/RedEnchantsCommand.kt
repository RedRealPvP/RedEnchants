package me.duro.redenchants.commands

import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.registry.CustomEnchants
import me.duro.redenchants.enchants.registry.CustomEnchants.generateEnchantBook
import me.duro.redenchants.enchants.registry.CustomEnchants.randomEnchantBook
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
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
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <give|book> [args]"))
            return true
        }

        val subcommands = listOf("give", "book")
        val subcommand = subcommands.find { it == (args?.get(0)) }

        if (subcommand == null) {
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <give|book> [args]"))
            return true
        }

        when (subcommand) {
            "give" -> {
                val enchant = CustomEnchants.allEnchants.find { it.key.key == args?.get(1) }

                if (enchant == null) {
                    sender.sendMessage(replaceColorCodes("&cThat enchantment does not exist."))
                    return true
                }

                val level = if (args?.size == 3) args[2].toInt() else 1

                if (level > enchant.maxLevel) {
                    sender.sendMessage(replaceColorCodes("&cThe max level for this enchantment is ${enchant.maxLevel}."))
                    return true
                }

                sender.inventory.addItem(generateEnchantBook(enchant, level))

                sender.sendMessage(replaceColorCodes("&7You have received a ${componentToString(enchant.displayName(level))}&7 enchantment!"))
            }

            "book" -> {
                val rarity = RedEnchantRarity.entries.find { it.name.lowercase() == args?.get(1) }

                if (rarity == null) {
                    sender.sendMessage(replaceColorCodes("&cThat rarity does not exist."))
                    return true
                }

                sender.inventory.addItem(randomEnchantBook(rarity))

                sender.sendMessage(replaceColorCodes("&7You have received a random ${rarity.color()}${lowerTitleCase(rarity.name)}&7 enchantment book!"))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<out String>
    ): List<String> {
        val subcommands = listOf("give", "book")

        if (args.size == 1) {
            return subcommands.filter { it.startsWith(args[0], true) }
        }

        val subcommand = subcommands.find { it.equals(args[0], true) } ?: return emptyList()

        when (subcommand) {
            "give" -> {
                if (args.size == 2) {
                    return CustomEnchants.allEnchants.map { it.key.key }.filter { it.startsWith(args[1], true) }
                }

                if (args.size == 3) {
                    val enchant = CustomEnchants.allEnchants.find { it.key.key == args[1] } ?: return emptyList()
                    return (1..enchant.maxLevel).map { it.toString() }.filter { it.startsWith(args[2], true) }
                }
            }

            "book" -> {
                if (args.size == 2) {
                    return RedEnchantRarity.entries.map { it.name.lowercase() }.filter { it.startsWith(args[1], true) }
                }
            }
        }

        return emptyList()
    }
}