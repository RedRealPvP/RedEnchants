package me.duro.redenchants.utils

fun titleCase(s: String) = s.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}

fun lowerTitleCase(s: String) = s.lowercase().replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}