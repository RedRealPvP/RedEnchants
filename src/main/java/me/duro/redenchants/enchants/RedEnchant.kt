package me.duro.redenchants.enchants

import io.papermc.paper.enchantments.EnchantmentRarity
import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.intToRoman
import me.duro.redenchants.utils.replaceColorCodes
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*

open class RedEnchant(
    private val name: String,
    private val maxLevel: Int = 1,
    private val itemTarget: EnchantmentTarget = EnchantmentTarget.BREAKABLE,
    private val conflictEnchants: List<RedEnchant> = emptyList(),
    val canEnchant: (ItemStack) -> Boolean = { _ -> true },
    private val activeSlots: List<EquipmentSlot> = listOf(EquipmentSlot.HAND),
) : Enchantment(NamespacedKey.minecraft(name)) {
    private val capitalized = name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    override fun translationKey(): String {
        return "${RedEnchants.instance.name}:$name"
    }

    @Deprecated("Enchant names are bad", ReplaceWith("translationKey()"))
    override fun getName(): String {
        return capitalized
    }

    override fun getMaxLevel(): Int {
        return maxLevel
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getItemTarget(): EnchantmentTarget {
        return itemTarget
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isCursed(): Boolean {
        return false
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        return conflictEnchants.contains(other)
    }

    override fun canEnchantItem(item: ItemStack): Boolean {
        return canEnchant(item)
    }

    override fun displayName(level: Int): Component {
        return replaceColorCodes("&7$capitalized ${intToRoman(level)}")
    }

    override fun isTradeable(): Boolean {
        return false
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.VERY_RARE
    }

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float {
        return 0.0f
    }

    override fun getActiveSlots(): MutableSet<EquipmentSlot> {
        return activeSlots.toMutableSet()
    }
}