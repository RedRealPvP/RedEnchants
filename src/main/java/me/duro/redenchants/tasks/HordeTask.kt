package me.duro.redenchants.tasks

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.listeners.HordeListener.Companion.generateHordeLocations
import me.duro.redenchants.listeners.HordeListener.Companion.isHordeMob
import me.duro.redenchants.listeners.HordeListener.Companion.spawnHorde
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class HordeTask(private val center: Location, private val radius: Double) : BukkitRunnable() {
    private var task: BukkitTask? = null

    init {
        runTaskTimer(RedEnchants.instance, 0L, 20 * 20L)
    }

    override fun run() {
        val locations = generateHordeLocations(center, radius, 25)

        RedEnchants.instance.server.broadcast(replaceColorCodes("&c&lA horde has spawned in the warzone!"))

        locations.forEach {
            spawnHorde(it)
        }

        task = object : BukkitRunnable() {
            override fun run() {
                removeHorde()
            }
        }.runTaskLater(RedEnchants.instance, 10 * 20L)
    }

    private fun removeHorde() {
        center.world.entities.forEach {
            if (isHordeMob(it)) {
                it.remove()
            }
        }

        task?.cancel()
    }
}