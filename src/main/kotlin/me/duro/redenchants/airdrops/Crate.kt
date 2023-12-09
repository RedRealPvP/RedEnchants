package me.duro.redenchants.airdrops

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.*
import org.bukkit.block.Barrel
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.util.Vector
import kotlin.random.Random

class Crate(private val location: Location, private val rarity: RedEnchantRarity) {
    private lateinit var fallingCrate: FallingBlock
    private lateinit var blockChest: Block

    fun drop() {
        val chestLocation = Location(
            location.world, location.blockX.toDouble(), location.blockY.toDouble(), location.blockZ.toDouble()
        )

        val parachuteLeash =
            (chestLocation.world.spawnEntity(chestLocation.add(0.0, 1.0, 0.0), EntityType.SLIME) as Slime).apply {
                setAI(false)
                size = 1
                isInvulnerable = true
                isInvisible = true
            }

        fallingCrate = chestLocation.world.spawnFallingBlock(chestLocation, Material.BARREL.createBlockData())

        val chickenParachutes = (0..4).map {
            (chestLocation.world.spawnEntity(
                chestLocation.add(Math.random() * 0.25, 1.0, Math.random() * 0.25), EntityType.CHICKEN
            ) as Chicken).apply {
                isInvulnerable = true
                setLeashHolder(parachuteLeash)
            }
        }

        fallingCrate.addPassenger(parachuteLeash)
        fallingCrate.setGravity(false)
        fallingCrate.dropItem = false

        Bukkit.getServer().scheduler.runTaskTimer(RedEnchants.instance, Runnable {
            if (fallingCrate.isDead) {
                chickenParachutes.forEach {
                    it.setLeashHolder(null)
                    val xVel = if (Math.random() < 0.5) Math.random() * 0.5 * -1 else Math.random() * 0.5
                    val zVel = if (Math.random() < 0.5) Math.random() * 0.5 * -1 else Math.random() * 0.5
                    it.velocity = Vector(xVel, 0.5, zVel)
                    Bukkit.getServer().scheduler.runTaskLater(RedEnchants.instance, Runnable { it.remove() }, 60)
                }

                parachuteLeash.remove()
                return@Runnable
            }

            repeat(3) {
                fallingCrate.world.playEffect(chestLocation.add(0.0, 1.0, 0.0), Effect.SMOKE, 0)
            }

            fallingCrate.velocity = Vector(0.0, -0.3, 0.0)
        }, 0, 2)

        CrateList.crateMap[fallingCrate] = this
    }

    fun spawnChest() {
        val chestLocation = Location(
            blockChest.world,
            blockChest.location.blockX.toDouble(),
            blockChest.location.blockY.toDouble(),
            blockChest.location.blockZ.toDouble()
        )

        chestLocation.block.type = Material.BARREL

        val barrel = chestLocation.block.state as Barrel
        val contents =
            RedEnchants.instance.crates.getCrate(rarity).filterNotNull().shuffled().take(Random.nextInt(3, 5)).map {
                it.clone().apply { amount = Random.nextInt(1, it.amount) }
            }

        barrel.customName(replaceColorCodes("${rarity.color()}&l${lowerTitleCase(rarity.name)} Crate"))
        barrel.update()

        contents.forEach {
            val slot = (0 until barrel.inventory.size).random()
            if (barrel.inventory.getItem(slot) != null) return@forEach
            barrel.inventory.setItem(slot, it)
        }


        CrateList.barrelList.add(barrel.location)
    }

    fun setBlockChest(block: Block) {
        blockChest = block
    }

    companion object {
        fun spawn(location: Location, rarity: RedEnchantRarity) {
            val chestLocation = Location(
                location.world, location.blockX.toDouble(), location.blockY.toDouble(), location.blockZ.toDouble()
            )

            Crate(chestLocation.add(0.0, 30.0, 0.0), rarity).drop()
        }
    }
}