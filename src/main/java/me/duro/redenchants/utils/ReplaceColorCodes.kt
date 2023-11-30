package me.duro.redenchants.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun replaceColorCodes(input: String) = LegacyComponentSerializer.legacyAmpersand().deserialize(input)
    .applyFallbackStyle(TextDecoration.ITALIC.withState(false))


fun componentToString(component: Component) = LegacyComponentSerializer.legacyAmpersand().serialize(component)
