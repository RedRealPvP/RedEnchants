package me.duro.redenchants.items

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
import me.duro.redenchants.utils.weightedRandom
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.EulerAngle

private fun customItem(
    displayName: String, data: Int, material: Material, lore: List<String> = emptyList()
): ItemStack {
    return ItemStack(material).apply {
        itemMeta = itemMeta.apply {
            displayName(replaceColorCodes(displayName))
            setCustomModelData(data)
            if (lore.isNotEmpty()) lore(lore.map { replaceColorCodes(it) })
        }
    }
}

enum class CrystalType {
    RUBY, SAPPHIRE, PERIDOT, ROSE_QUARTZ, TOPAZ;

    fun color() = when (this) {
        RUBY -> "&c"
        SAPPHIRE -> "&9"
        PERIDOT -> "&a"
        ROSE_QUARTZ -> "&d"
        TOPAZ -> "&e"
    }

    fun rarity() = when (this) {
        RUBY -> 0.1
        SAPPHIRE -> 0.3
        PERIDOT -> 0.6
        TOPAZ -> 0.8
        ROSE_QUARTZ -> 1.0
    }

    fun displayName() = "${color()}${name.split("_").joinToString(" ") { lowerTitleCase(it) }}"
}

class Crystal(val type: CrystalType) {
    val crystalItem = customItem("${type.displayName()} Crystal", type.ordinal + 1, Material.AMETHYST_CLUSTER)

    val gemstoneItem = customItem(
        "${type.displayName()} Gemstone",
        type.ordinal + 1,
        Material.AMETHYST_SHARD,
        listOf("&7Can be ${type.color()}sold&7 or used to ${type.color()}craft&7 custom items.")
    )

    val enchantedGemstoneItem = customItem(
        "${type.color()}Enchanted ${type.displayName()} Gemstone",
        type.ordinal + 1,
        Material.AMETHYST_SHARD,
        listOf("&7Can be ${type.color()}sold&7 or used to ${type.color()}craft&7 custom items.")
    ).apply {
        addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1)
        itemMeta = itemMeta.apply { addItemFlags(ItemFlag.HIDE_ENCHANTS) }
    }

    companion object {
        val fragmentItem = customItem("&5Crystal Fragment", 1, Material.PRISMARINE_SHARD, listOf("&7TODO"))

        private fun compare(item: ItemStack, other: ItemStack) =
            componentToString(item.itemMeta.displayName()!!) == componentToString(other.itemMeta.displayName()!!) && item.itemMeta.customModelData == other.itemMeta.customModelData

        fun typeFromItem(item: ItemStack) = CrystalType.entries.firstOrNull {
            val crystal = Crystal(it).crystalItem
            val gemstone = Crystal(it).gemstoneItem
            compare(item, crystal) || compare(item, gemstone)
        }

        fun spawn(location: Location, type: CrystalType, blockFace: BlockFace = BlockFace.UP) {
            val shift = when (blockFace) {
                BlockFace.DOWN -> Triple(0.0, 0.2, 0.0)
                BlockFace.UP -> Triple(0.0, -0.1, 0.0)
                BlockFace.NORTH -> Triple(0.0, 0.1, 0.15)
                BlockFace.SOUTH -> Triple(0.0, 0.1, -0.15)
                BlockFace.EAST -> Triple(-0.15, 0.1, 0.0)
                BlockFace.WEST -> Triple(0.15, 0.1, 0.0)
                else -> Triple(0.0, 0.0, 0.0)
            }

            val headRotation = when (blockFace) {
                BlockFace.UP -> EulerAngle.ZERO
                BlockFace.DOWN -> EulerAngle(Math.toRadians(180.0), 0.0, 0.0)
                BlockFace.NORTH -> EulerAngle(Math.toRadians(270.0), 0.0, Math.toRadians(90.0))
                BlockFace.SOUTH -> EulerAngle(Math.toRadians(90.0), 0.0, Math.toRadians(90.0))
                BlockFace.EAST -> EulerAngle(0.0, 0.0, Math.toRadians(90.0))
                BlockFace.WEST -> EulerAngle(0.0, 0.0, Math.toRadians(270.0))
                else -> EulerAngle.ZERO
            }

            val loc = Location(
                location.world,
                location.blockX + 0.5 + shift.first,
                location.blockY + shift.second - 1,
                location.blockZ + 0.5 + shift.third
            )

            val armorStand = location.world?.spawn(loc, ArmorStand::class.java) ?: return

            armorStand.headPose = headRotation
            armorStand.equipment.helmet = Crystal(type).crystalItem
            armorStand.isInvisible = true
            armorStand.setGravity(false)
            armorStand.setDisabledSlots(*EquipmentSlot.entries.toTypedArray())
            armorStand.persistentDataContainer.set(
                NamespacedKey(RedEnchants.instance, "is_crystal"), PersistentDataType.BYTE, 1.toByte()
            )
        }

        private fun nearestBlock(center: Location, radius: Int, type: Material): List<Location> {
            val locations = mutableListOf<Location>()

            val world = center.world

            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val block = world.getBlockAt(center.blockX + x, center.blockY + y, center.blockZ + z)

                        if (block.type == type) {
                            locations.add(block.location)
                        }
                    }
                }
            }

            return locations
        }

        private fun crystalLocations(center: Location, radius: Int): Map<Location, List<Pair<Location, BlockFace>>>? {
            val sponges = nearestBlock(center, radius, Material.SPONGE)
            if (sponges.isEmpty()) return null

            val stone = sponges.associateWith { nearestBlock(it, 2, Material.DEEPSLATE) }
            if (stone.isEmpty()) return null

            val air = stone.mapValues { (_, value) ->
                value.map {
                    val faces = listOf(
                        BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH
                    ).reversed()

                    val weights = faces.associateWith { face ->
                        val block = it.block.getRelative(face)
                        if (block.type == Material.AIR) 1 + faces.indexOf(face).toDouble() * 2 else 0.0
                    }

                    val facesWithWeight = adjacentAir(it).associateWith { (_, face) ->
                        weights[face] ?: 0.0
                    }.map { (l, w) -> l to w }

                    weightedRandom(facesWithWeight)
                }
            }

            return air
        }

        private fun adjacentAir(startLocation: Location): List<Pair<Location, BlockFace>> {
            val faces =
                listOf(BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH)

            return faces.mapNotNull {
                val block = startLocation.block.getRelative(it)
                if (block.type == Material.AIR) Pair(block.location, it)
                else null
            }
        }

        fun generateRandomCrystals(center: Location, radius: Int) {
            val locations = crystalLocations(center, radius) ?: return

            locations.mapValues { (_, v) ->
                val (location, face) = v.first()
                val type = weightedRandom(CrystalType.entries.map { it to it.rarity() })
                spawn(location, type, face)
            }
        }
    }
}