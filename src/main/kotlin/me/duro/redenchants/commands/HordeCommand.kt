package me.duro.redenchants.commands

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.airdrops.CrateList
import me.duro.redenchants.airdrops.airdropGUI
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.items.hordeFlare
import me.duro.redenchants.listeners.HordeListener
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class HordeCommand : CommandExecutor, TabExecutor {
    private val subcommands = listOf("give", "clear", "crate")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        val subcommand = args.getOrNull(0)

        if (subcommand == null || !subcommands.contains(subcommand)) {
            sender.sendMessage(replaceColorCodes("&cUsage: /$label <give|clear|crate>"))
            return true
        }

        when (subcommand) {
            "give" -> {
                val player = sender.server.getPlayer(args.getOrNull(1) ?: sender.name) ?: sender

                player.inventory.addItem(hordeFlare)
                player.sendMessage(replaceColorCodes("&7You have been given a&c horde flare&7."))

                if (player != sender) {
                    sender.sendMessage(replaceColorCodes("&7You have given a&c horde flare&7 to &c${player.name}&7."))
                }
            }

            "clear" -> {
                sender.world.entities.forEach {
                    if (it.persistentDataContainer.has(HordeListener.hordeKey, PersistentDataType.BYTE)) {
                        it.remove()
                    }
                }

                CrateList.barrelList.forEach {
                    it.block.type = Material.AIR
                }

                CrateList.barrelList.clear()

                sender.sendMessage(replaceColorCodes("&cCleared all horde entities."))
            }

            "crate" -> {
                val rarity = RedEnchantRarity.entries.find { it.name.equals(args.getOrNull(1), true) }

                if (rarity == null) {
                    sender.sendMessage(replaceColorCodes("&cThat rarity does not exist."))
                    return true
                }

                val gui = airdropGUI(rarity)
                sender.openInventory(gui)
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
                    RedEnchants.instance.server.onlinePlayers.map { it.name }.filter { it.startsWith(args[1], true) }

                if (args.size == 2) players else emptyList()
            }

            "crate" -> {
                val rarities = RedEnchantRarity.entries.map { it.name }.filter { it.startsWith(args[1], true) }

                if (args.size == 2) rarities else emptyList()
            }

            else -> emptyList()
        }
    }
}