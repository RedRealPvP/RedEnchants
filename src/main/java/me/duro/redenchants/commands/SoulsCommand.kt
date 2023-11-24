package me.duro.redenchants.commands

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class SoulsCommand : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if (args.isEmpty() || args.size < 2) {
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <get|set|add|remove> <player> [amount]"))
            return true
        }

        val player = sender.server.getPlayer(args[1])!!

        when (args[0]) {
            "get" -> {
                sender.sendMessage(replaceColorCodes("&cSouls: ${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}"))
            }

            "set" -> {
                if (args.size < 3) {
                    sender.sendMessage(replaceColorCodes("&cUsage: /$label set <player> <amount>"))
                    return true
                }

                RedEnchants.instance.soulsManager.setSouls(player.uniqueId, args[2].toInt())
                sender.sendMessage("Souls: ${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}")
            }

            "add" -> {
                if (args.size < 3) {
                    sender.sendMessage(replaceColorCodes("&cUsage: /$label add <player> <amount>"))
                    return true
                }

                RedEnchants.instance.soulsManager.addSouls(player.uniqueId, args[2].toInt())
                sender.sendMessage(replaceColorCodes("&cSouls: ${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}"))
            }

            "remove" -> {
                if (args.size < 3) {
                    sender.sendMessage(replaceColorCodes("&cUsage: /$label remove <player> <amount>"))
                    return true
                }

                RedEnchants.instance.soulsManager.removeSouls(player.uniqueId, args[2].toInt())
                sender.sendMessage(replaceColorCodes("&cSouls: ${RedEnchants.instance.soulsManager.getSouls(player.uniqueId)}"))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        val players = sender.server.onlinePlayers.map { it.name }
        val subCommands = listOf("get", "set", "add", "remove")

        if (args.size == 1) {
            return subCommands.filter { it.startsWith(args[0]) }
        }

        if (args.size == 2) {
            return players.filter { it.startsWith(args[1]) }
        }

        return emptyList()
    }
}