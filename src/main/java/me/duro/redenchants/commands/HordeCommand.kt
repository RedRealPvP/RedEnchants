package me.duro.redenchants.commands

import me.duro.redenchants.listeners.HordeListener.Companion.generateHordeLocations
import me.duro.redenchants.listeners.HordeListener.Companion.spawnHorde
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class HordeCommand: CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        val center = Location(sender.world, -186.0, 88.0, 495.0)
        val locations = generateHordeLocations(center, 150.0, 25)

        locations.forEach {
            spawnHorde(it)
            sender.sendMessage(replaceColorCodes("&cSpawned horde at &4${it.x}&c, &4${it.y}&c, &4${it.z}&c."))
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        return listOf()
    }
}