package me.duro.redenchants.apis

import me.duro.redenchants.RedEnchants
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.permission.Permission
import org.bukkit.OfflinePlayer

open class VaultHook {
    private lateinit var economy: Economy
    private lateinit var permissions: Permission
    private lateinit var chat: Chat

    init {
        setupEconomy()
        setupChat()
        setupPermissions()
    }

    private fun setupEconomy() {
        val rsp = RedEnchants.instance.server.servicesManager.getRegistration(Economy::class.java)

        if (rsp != null) economy = rsp.provider
    }

    private fun setupChat() {
        val rsp = RedEnchants.instance.server.servicesManager.getRegistration(Chat::class.java)

        if (rsp != null) chat = rsp.provider
    }

    private fun setupPermissions() {
        val rsp = RedEnchants.instance.server.servicesManager.getRegistration(Permission::class.java)

        if (rsp != null) permissions = rsp.provider
    }

    fun withdraw(player: OfflinePlayer, amount: Double): EconomyResponse = economy.withdrawPlayer(player, amount)

    fun deposit(player: OfflinePlayer, amount: Double): EconomyResponse = economy.depositPlayer(player, amount)

    fun has(player: OfflinePlayer, amount: Double) = economy.has(player, amount)

    fun balance(player: OfflinePlayer) = economy.getBalance(player)
}
