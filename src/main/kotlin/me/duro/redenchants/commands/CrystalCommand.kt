package me.duro.redenchants.commands

import me.duro.redenchants.items.Crystal
import me.duro.redenchants.items.Crystal.Companion.generateRandomCrystals
import me.duro.redenchants.items.CrystalType
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CrystalCommand : CommandExecutor, TabExecutor {
    private val subcommands = listOf("gemstone", "crystal", "regen")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if (args.isEmpty() || args.size == 1 && args[0].isEmpty()) {
            sender.sendMessage(replaceColorCodes("&cUsage: /crystals <gemstone|crystal|regen>"))
        }

        val subcommand = args[0]

        if (!subcommands.contains(subcommand)) {
            sender.sendMessage(replaceColorCodes("&cUsage: /crystals <gemstone|crystal|regen>"))
            return true
        }

        when (subcommand) {
            "gemstone" -> {
                if (args.size < 2) {
                    sender.sendMessage(replaceColorCodes("&cUsage: /crystals gemstone <ruby|sapphire|peridot|rose_quartz|topaz> <amount>"))
                    return true
                }

                val type = args[1]

                if (CrystalType.entries.none { it.name.equals(type, true) }) {
                    sender.sendMessage(replaceColorCodes("&cInvalid gemstone. <ruby|sapphire|peridot|rose_quartz|topaz>"))
                    return true
                }

                val amount = if (args.size == 3) args[2].toIntOrNull() ?: 1 else 1

                if (amount < 1) {
                    sender.sendMessage(replaceColorCodes("&cInvalid amount."))
                    return true
                }

                val item = Crystal(CrystalType.valueOf(type.uppercase())).gemstoneItem.also { it.amount = amount }
                sender.inventory.addItem(item)
            }

            "crystal" -> {
                if (args.size < 2) {
                    sender.sendMessage(replaceColorCodes("&cUsage: /crystals crystal <ruby|sapphire|peridot|rose_quartz|topaz>"))
                    return true
                }

                val type = args[1]
                val crystalType = CrystalType.entries.firstOrNull { it.name.equals(type, true) }

                if (crystalType == null) {
                    sender.sendMessage(replaceColorCodes("&cInvalid crystal. <ruby|sapphire|peridot|rose_quartz|topaz>"))
                    return true
                }

                sender.sendMessage(replaceColorCodes("&7Spawned a ${crystalType.displayName()}&7 crystal."))
                Crystal.spawn(sender.location, crystalType)
            }

            "regen" -> {
                generateRandomCrystals(sender.location, 50)
                sender.sendMessage(replaceColorCodes("&7Regenerated all crystals."))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        if (args.size == 1) {
            return subcommands.filter { it.startsWith(args[0], true) }
        }

        if (args.size == 2 && args[0].equals("gemstone", true) || args[0].equals("crystal", true)) {
            return CrystalType.entries.map { it.name.lowercase() }.filter { it.startsWith(args[1], true) }
        }

        return emptyList()
    }
}