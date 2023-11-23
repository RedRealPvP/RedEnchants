package me.duro.redenchants.utils

import me.duro.redenchants.RedEnchants
import net.coreprotect.CoreProtect

import net.coreprotect.CoreProtectAPI


fun getCoreProtect(): CoreProtectAPI? {
    val plugin = RedEnchants.instance.server.pluginManager.getPlugin("CoreProtect")

    if (plugin == null || plugin !is CoreProtect) {
        return null
    }

    if (!plugin.api.isEnabled) {
        return null
    }

    return if (plugin.api.APIVersion() < 9) null else plugin.api
}