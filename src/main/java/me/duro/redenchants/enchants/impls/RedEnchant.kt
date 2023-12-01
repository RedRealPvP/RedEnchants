package me.duro.redenchants.enchants.impls

import io.papermc.paper.enchantments.EnchantmentRarity
import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.intToRoman
import me.duro.redenchants.utils.replaceColorCodes
import me.duro.redenchants.utils.titleCase
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

enum class RedEnchantRarity {
    COMMON, UNCOMMON, RARE, FABULOUS, EXOTIC, ADMIN;

    fun color(): String {
        return when (this) {
            COMMON -> "&7"
            UNCOMMON -> "&a"
            RARE -> "&9"
            FABULOUS -> "&5"
            EXOTIC -> "&6"
            ADMIN -> "&c"
        }
    }

    fun cost(): Int {
        return when (this) {
            COMMON -> 10
            UNCOMMON -> 20
            RARE -> 30
            FABULOUS -> 40
            EXOTIC -> 50
            ADMIN -> 100000
        }
    }
}

enum class RedEnchantTarget {
    HELMET, CHESTPLATE, LEGGINGS, BOOTS, SWORD, AXE, PICKAXE, SHOVEL, HOE, TOOL, BOW, FISHING_ROD, TRIDENT;

    fun match(item: ItemStack): Boolean {
        val axes = arrayOf(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
        )

        val picks = arrayOf(
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE
        )

        val shovels = arrayOf(
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL
        )

        val hoes = arrayOf(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
        )

        return when (this) {
            HELMET -> EnchantmentTarget.ARMOR_HEAD.includes(item)
            CHESTPLATE -> EnchantmentTarget.ARMOR_TORSO.includes(item)
            LEGGINGS -> EnchantmentTarget.ARMOR_LEGS.includes(item)
            BOOTS -> EnchantmentTarget.ARMOR_FEET.includes(item)
            SWORD -> EnchantmentTarget.WEAPON.includes(item)
            AXE -> axes.contains(item.type)
            PICKAXE -> picks.contains(item.type)
            SHOVEL -> shovels.contains(item.type)
            HOE -> hoes.contains(item.type)
            TOOL -> EnchantmentTarget.TOOL.includes(item)
            BOW -> EnchantmentTarget.BOW.includes(item)
            FISHING_ROD -> EnchantmentTarget.FISHING_ROD.includes(item)
            TRIDENT -> EnchantmentTarget.TRIDENT.includes(item)
        }
    }
}

open class RedEnchant(
    private val name: String,
    val description: (Int) -> String,
    private val maxLevel: Int = 1,
    private val conflictEnchants: List<RedEnchant> = emptyList(),
    val canEnchant: (ItemStack) -> Boolean = { _ -> true },
    private val activeSlots: List<EquipmentSlot> = listOf(EquipmentSlot.HAND),
    val enchantRarity: RedEnchantRarity = RedEnchantRarity.COMMON,
) : Enchantment(NamespacedKey.minecraft(name)) {
    private val capitalized = name.replace("_", " ").replace("-", " ").split(" ").joinToString(" ") { titleCase(it) }

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
        return EnchantmentTarget.BREAKABLE
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
        val color = enchantRarity.color()
        return if (level == 1 && this.maxLevel == 1) replaceColorCodes("$color$capitalized")
        else replaceColorCodes("$color$capitalized ${intToRoman(level)}")
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