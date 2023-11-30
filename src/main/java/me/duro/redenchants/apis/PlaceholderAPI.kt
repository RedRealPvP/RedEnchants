package me.duro.redenchants.apis

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.duro.redenchants.RedEnchants
import org.bukkit.OfflinePlayer

class SoulsPlaceholder : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "redenchants"
    }

    override fun getAuthor(): String {
        return "Duro"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null) return null

        return when (params) {
            "souls" -> RedEnchants.instance.soulsManager.getSouls(player.uniqueId).toString()
            else -> null
        }
    }
}