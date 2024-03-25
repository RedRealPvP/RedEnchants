package me.duro.redenchants.utils

fun <T> weightedRandom(items: List<Pair<T, Double>>): T {
    val total = items.sumOf { it.second }
    var random = Math.random() * total

    for (item in items) {
        random -= item.second
        if (random <= 0) return item.first
    }

    return items[items.size - 1].first
}