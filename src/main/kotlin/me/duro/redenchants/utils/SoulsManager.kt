package me.duro.redenchants.utils

import me.duro.redenchants.RedEnchants
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

data class PlayerData(val soulsCount: Int = 0)

class SoulsManager {
    private lateinit var data: MutableMap<UUID, PlayerData>

    fun load(): SoulsManager {
        val file = File(RedEnchants.instance.dataFolder, "souls.yml")

        if (!file.exists()) RedEnchants.instance.saveResource("souls.yml", false)

        val soulsFile = YamlConfiguration()
        soulsFile.options().parseComments(true)

        try {
            soulsFile.load(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        data = mutableMapOf()

        soulsFile.getKeys(false).forEach { uuidString ->
            val uuid = UUID.fromString(uuidString)
            val soulsCount = soulsFile.getInt(uuidString, 0)
            data[uuid] = PlayerData(soulsCount)
        }

        return this
    }

    private fun save() {
        val file = File(RedEnchants.instance.dataFolder, "souls.yml")

        if (!file.exists()) {
            RedEnchants.instance.saveResource("souls.yml", false)
        }

        val soulsFile = YamlConfiguration()
        soulsFile.options().parseComments(true)

        try {
            soulsFile.load(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        data.forEach { (uuid, playerData) ->
            soulsFile.set(uuid.toString(), playerData.soulsCount)
        }

        Bukkit.getScheduler().runTaskAsynchronously(RedEnchants.instance, Runnable {
            soulsFile.save(file)
        })
    }

    fun getSouls(player: UUID): Int {
        return data[player]?.soulsCount ?: 0
    }

    fun setSouls(player: UUID, souls: Int) {
        data[player] = PlayerData(souls)
        save()
    }

    fun addSouls(player: UUID, souls: Int) {
        val currentSouls = getSouls(player)
        setSouls(player, currentSouls + souls)
    }

    fun removeSouls(player: UUID, souls: Int) {
        val currentSouls = getSouls(player)
        setSouls(player, currentSouls - souls)
    }

    companion object {
        fun load() = SoulsManager().load()
    }
}