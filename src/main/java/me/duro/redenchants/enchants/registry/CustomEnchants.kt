package me.duro.redenchants.enchants.registry

import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchant
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import me.duro.redenchants.enchants.impls.armor.NocturnalEnchant
import me.duro.redenchants.enchants.impls.armor.NourishEnchant
import me.duro.redenchants.enchants.impls.bows.ShotgunEnchant
import me.duro.redenchants.enchants.impls.fishing.DoubleCatchEnchant
import me.duro.redenchants.enchants.impls.tools.TelepathyEnchant
import me.duro.redenchants.enchants.impls.tools.TimberEnchant
import me.duro.redenchants.enchants.impls.tools.VeinMinerEnchant
import me.duro.redenchants.enchants.types.*
import me.duro.redenchants.enchants.impls.weapons.DecapitatorEnchant
import me.duro.redenchants.enchants.impls.weapons.VampiricEnchant
import me.duro.redenchants.tasks.PassiveEnchantsTask
import me.duro.redenchants.utils.addLore
import me.duro.redenchants.utils.componentToString
import me.duro.redenchants.utils.lowerTitleCase
import me.duro.redenchants.utils.replaceColorCodes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Suppress("MemberVisibilityCanBePrivate")
object CustomEnchants {
    val TELEPATHY = TelepathyEnchant()
    val TIMBER = TimberEnchant()
    val SHOTGUN = ShotgunEnchant()
    val DECAPITATOR = DecapitatorEnchant()
    val VEIN_MINER = VeinMinerEnchant()
    val VAMPIRIC = VampiricEnchant()
    val DOUBLE_CATCH = DoubleCatchEnchant()
    val NOCTURNAL = NocturnalEnchant()
    val NOURISH = NourishEnchant()

    val allEnchants = listOf<RedEnchant>(
        TELEPATHY,
        TIMBER,
        SHOTGUN,
        DECAPITATOR,
        VEIN_MINER,
        VAMPIRIC,
        DOUBLE_CATCH,
        NOCTURNAL,
        NOURISH,
    )

    val enchantsMap = hashMapOf<Class<out EnchantType>, MutableSet<in RedEnchant>>()
    val passiveEnchantsTask = PassiveEnchantsTask()

    fun register() {
        allEnchants.forEach { registerEnchant(it) }

        registerType(GenericEnchant::class.java)
        registerType(PassiveEnchant::class.java)

        registerWrapper(BlockBreakEvent::class.java, BlockBreakEnchant::class.java, DataGathers.BLOCK_BREAK)
        registerWrapper(BlockDropItemEvent::class.java, BlockDropEnchant::class.java, DataGathers.BLOCK_DROP)
        registerWrapper(EntityShootBowEvent::class.java, BowEnchant::class.java, DataGathers.BOW_SHOOT)
        registerWrapper(ProjectileHitEvent::class.java, BowEnchant::class.java, DataGathers.PROJECTILE_HIT)
        registerWrapper(EntityDamageByEntityEvent::class.java, BowEnchant::class.java, DataGathers.ENTITY_DAMAGE_SHOOT)
        registerWrapper(
            EntityDamageByEntityEvent::class.java, CombatEnchant::class.java, DataGathers.ENTITY_DAMAGE_ATTACK
        )
        registerWrapper(
            EntityDamageByEntityEvent::class.java, CombatEnchant::class.java, DataGathers.ENTITY_DAMAGE_DEFENSE
        )
        registerWrapper(EntityDeathEvent::class.java, DeathEnchant::class.java, DataGathers.ENTITY_KILL)
        registerWrapper(EntityDeathEvent::class.java, DeathEnchant::class.java, DataGathers.ENTITY_DEATH)
        registerWrapper(PlayerFishEvent::class.java, FishingEnchant::class.java, DataGathers.FISHING)
        registerWrapper(PlayerInteractEvent::class.java, InteractEnchant::class.java, DataGathers.INTERACT)

        passiveEnchantsTask.start()
    }

    private fun registerEnchantType(enchant: RedEnchant): Boolean {
        val enchantClass = enchant.javaClass

        if (enchantClass.interfaces.isEmpty()) {
            println("Enchant does not implement any interfaces.")
            return false
        }

        enchantClass.interfaces.forEach { enchantsMap[it]?.add(enchant) }

        return true
    }

    fun <T : EnchantType> registerType(enchantClass: Class<T>) {
        enchantsMap.computeIfAbsent(enchantClass) { mutableSetOf() }
    }

    fun <E : Event?, T : EnchantType> registerWrapper(
        eventClass: Class<E>, enchantClass: Class<T>, dataGather: DataGather<E, T>
    ) {
        for (priority in EventPriority.entries) {
            val event = WrappedEvent(priority, eventClass, enchantClass, dataGather)

            RedEnchants.instance.server.pluginManager.registerEvent(
                eventClass, event, priority, event, RedEnchants.instance, true
            )
        }

        registerType(enchantClass)
    }

    private fun registerEnchant(enchant: RedEnchant) {
        try {
            val f = Enchantment::class.java.getDeclaredField("acceptingNew")
            f.isAccessible = true
            f.set(null, true)
            registerEnchantType(enchant)

            if (Enchantment.getByKey(enchant.key) != null) {
                return println("Enchantment ${enchant.key} is already registered.")
            }

            Enchantment.registerEnchantment(enchant)
            println("Registered ${enchant.key} enchantment.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateEnchantBook(enchant: RedEnchant, level: Int): ItemStack {
        val lore = mutableListOf(componentToString(enchant.displayName(level)), "&8▸ ${enchant.description(level)}")

        return ItemStack(Material.ENCHANTED_BOOK).also {
            it.addUnsafeEnchantment(enchant, level)

            it.itemMeta = it.itemMeta!!.apply {
                displayName(replaceColorCodes("${enchant.enchantRarity.color()}Enchanted Book"))
                lore(lore.map { l -> replaceColorCodes(l) })

                persistentDataContainer.set(
                    NamespacedKey(RedEnchants.instance, "red_enchant"), PersistentDataType.STRING, enchant.key.key
                )

                persistentDataContainer.set(
                    NamespacedKey(RedEnchants.instance, "red_enchant_level"), PersistentDataType.INTEGER, level
                )
            }
        }
    }

    fun randomEnchantBook(rarity: RedEnchantRarity) = ItemStack(Material.ENCHANTED_BOOK).also {
        it.itemMeta = it.itemMeta.apply {
            displayName(replaceColorCodes("${rarity.color()}&lRandom ${lowerTitleCase(rarity.name)} Enchant"))

            lore(
                listOf(
                    replaceColorCodes(
                        "&7Right click to receive a random ${rarity.color()}${lowerTitleCase(rarity.name)}&7 enchant."
                    ),
                )
            )
        }
    }

    fun addEnchant(item: ItemStack, enchant: RedEnchant, level: Int): ItemStack {
        item.addUnsafeEnchantment(enchant, level)
        addLore(item, enchant.displayName(level))

        return item
    }
}