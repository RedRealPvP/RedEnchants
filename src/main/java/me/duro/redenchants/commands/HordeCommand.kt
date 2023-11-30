package me.duro.redenchants.commands

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.items.hordeFlare
import me.duro.redenchants.listeners.HordeListener
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class HordeCommand : CommandExecutor, TabExecutor {
    private val subcommands = listOf("give", "clear")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        val subcommand = args.getOrNull(0)

        if (subcommand == null || !subcommands.contains(subcommand)) {
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <give|clear>"))
            return true
        }

        when (subcommand) {
            "give" -> {
                val player = sender.server.getPlayer(args.getOrNull(1) ?: sender.name) ?: sender

                player.inventory.addItem(hordeFlare)
                player.sendMessage(replaceColorCodes("&cYou have been given a horde flare."))

                if (player != sender) {
                    sender.sendMessage(replaceColorCodes("&cYou have given a horde flare to ${player.name}."))
                }
            }

            "clear" -> {
                sender.world.entities.forEach {
                    if (it.persistentDataContainer.has(HordeListener.hordeKey, PersistentDataType.BYTE)) {
                        it.remove()
                    }
                }

                sender.sendMessage(replaceColorCodes("&cCleared all horde entities."))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        val subcommands = subcommands.filter { it.startsWith(args[0]) }

        if (args.size == 1) return subcommands

        val subcommand = subcommands.firstOrNull { it.equals(args[0], true) } ?: return emptyList()

        return when (subcommand) {
            "give" -> {
                val players =
                    RedEnchants.instance.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[1]) }

                if (args.size == 2) players else emptyList()
            }

            else -> emptyList()
        }
    }
}