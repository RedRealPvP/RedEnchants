package me.duro.redenchants.tasks

import me.duro.redenchants.RedEnchants

abstract class AbstractTask {
    private var taskId = -1
    abstract val interval: Long
    abstract val async: Boolean

    abstract fun action()

    fun start(): Boolean {
        if (taskId >= 0 || interval <= 0) return false

        taskId = if (async) RedEnchants.instance.server.scheduler.runTaskTimerAsynchronously(
            RedEnchants.instance, ::action, 1L, interval
        ).taskId
        else RedEnchants.instance.server.scheduler.runTaskTimer(RedEnchants.instance, ::action, 1L, interval).taskId

        return true
    }
}