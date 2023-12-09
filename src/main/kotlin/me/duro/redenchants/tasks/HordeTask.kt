package me.duro.redenchants.tasks

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.airdrops.Crate
import me.duro.redenchants.airdrops.CrateList
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.listeners.HordeListener.Companion.generateHordeLocations
import me.duro.redenchants.listeners.HordeListener.Companion.isHordeMob
import me.duro.redenchants.listeners.HordeListener.Companion.spawnHorde
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Barrel
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask


class HordeTask(private val center: Location, private val radius: Double) : BukkitRunnable() {
    private var task: BukkitTask? = null
    private val config = RedEnchants.instance.config.data.horde

    init {
        runTaskTimer(RedEnchants.instance, 0L, config.interval * 360 * 20L)
    }

    override fun run() {
        val locations = generateHordeLocations(center, 100.0, radius, 25)

        RedEnchants.instance.server.broadcast(replaceColorCodes(config.messages.spawn))

        locations.forEach {
            spawnHorde(it)
            Crate.spawn(it, RedEnchantRarity.entries.minus(RedEnchantRarity.ADMIN).random())
        }

        task = object : BukkitRunnable() {
            override fun run() {
                removeHorde()
            }
        }.runTaskLater(RedEnchants.instance, config.duration * 360 * 20L)
    }

    fun removeHorde() {
        center.world.entities.forEach {
            if (isHordeMob(it)) {
                it.remove()
            }
        }

        CrateList.barrelList.forEach {
            val barrel = it.block.state as Barrel
            barrel.inventory.clear()
            it.block.type = Material.AIR
        }

        CrateList.barrelList.clear()

        task?.cancel()
    }

    companion object {
        fun removeHorde() {
            HordeTask(Location(null, 0.0, 0.0, 0.0), 0.0).removeHorde()
        }
    }
}