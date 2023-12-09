package me.duro.redenchants.enchants.registry

import me.duro.redenchants.enchants.types.*
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object DataGathers {
    val BLOCK_BREAK = object : DataGather<BlockBreakEvent, BlockBreakEnchant>() {
        override fun checkPriority(enchant: BlockBreakEnchant, priority: EventPriority): Boolean {
            return enchant.breakPriority == priority
        }

        override fun getEnchantSlots(event: BlockBreakEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND)
        }

        override fun useEnchant(
            event: BlockBreakEvent, entity: LivingEntity, item: ItemStack, enchant: BlockBreakEnchant, level: Int
        ): Boolean {
            return enchant.onBreak(event, entity, item, level)
        }

        override fun getEntity(event: BlockBreakEvent?): LivingEntity? {
            return event?.player
        }
    }

    val BLOCK_DROP = object : DataGather<BlockDropItemEvent, BlockDropEnchant>() {
        override fun checkPriority(enchant: BlockDropEnchant, priority: EventPriority): Boolean {
            return enchant.dropPriority == priority
        }

        override fun getEnchantSlots(event: BlockDropItemEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND)
        }

        override fun useEnchant(
            event: BlockDropItemEvent, entity: LivingEntity, item: ItemStack, enchant: BlockDropEnchant, level: Int
        ): Boolean {
            return enchant.onDrop(event, entity, item, level)
        }

        override fun getEntity(event: BlockDropItemEvent?): LivingEntity? {
            return event?.player
        }
    }

    val BOW_SHOOT = object : DataGather<EntityShootBowEvent, BowEnchant>() {
        override fun checkPriority(enchant: BowEnchant, priority: EventPriority): Boolean {
            return enchant.shootPriority == priority
        }

        override fun getEnchantSlots(event: EntityShootBowEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)
        }

        override fun useEnchant(
            event: EntityShootBowEvent, entity: LivingEntity, item: ItemStack, enchant: BowEnchant, level: Int
        ): Boolean {
            return enchant.onShoot(event, entity, item, level)
        }

        override fun getEntity(event: EntityShootBowEvent?): LivingEntity? {
            return event?.entity
        }
    }

    val PROJECTILE_HIT = object : DataGather<ProjectileHitEvent, BowEnchant>() {
        override fun checkPriority(enchant: BowEnchant, priority: EventPriority): Boolean {
            return enchant.hitPriority == priority
        }

        override fun getEnchantSlots(event: ProjectileHitEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)
        }

        override fun useEnchant(
            event: ProjectileHitEvent, entity: LivingEntity, item: ItemStack, enchant: BowEnchant, level: Int
        ): Boolean {
            return enchant.onHit(event, entity, event.entity, item, level)
        }

        override fun getEntity(event: ProjectileHitEvent?): LivingEntity? {
            return if (event?.entity?.shooter is LivingEntity) event.entity.shooter as LivingEntity else null
        }
    }

    val ENTITY_DAMAGE_SHOOT = object : DataGather<EntityDamageByEntityEvent, BowEnchant>() {
        override fun checkPriority(enchant: BowEnchant, priority: EventPriority): Boolean {
            return enchant.damagePriority == priority
        }

        override fun getEnchantSlots(event: EntityDamageByEntityEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)
        }

        override fun useEnchant(
            event: EntityDamageByEntityEvent, entity: LivingEntity, item: ItemStack, enchant: BowEnchant, level: Int
        ): Boolean {
            if (event.damager !is Projectile) return false
            return if (event.entity !is LivingEntity || event.cause == EntityDamageEvent.DamageCause.CUSTOM) false else enchant.onDamage(
                event, event.damager as Projectile, entity, event.entity as LivingEntity, item, level
            )
        }

        override fun getEntity(event: EntityDamageByEntityEvent?): LivingEntity? {
            return if (event!!.damager is Projectile && (event.damager as Projectile).shooter is LivingEntity) {
                (event.damager as Projectile).shooter as LivingEntity
            } else null
        }
    }

    val ENTITY_DAMAGE_ATTACK = object : DataGather<EntityDamageByEntityEvent, CombatEnchant>() {
        override fun checkPriority(enchant: CombatEnchant, priority: EventPriority): Boolean {
            return enchant.attackPriority == priority
        }

        override fun getEnchantSlots(event: EntityDamageByEntityEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND)
        }

        override fun useEnchant(
            event: EntityDamageByEntityEvent, entity: LivingEntity, item: ItemStack, enchant: CombatEnchant, level: Int
        ): Boolean {
            if (event.cause == EntityDamageEvent.DamageCause.THORNS || event.cause == EntityDamageEvent.DamageCause.CUSTOM) return false
            return if (event.entity !is LivingEntity) false else enchant.onAttack(
                event, entity, event.entity as LivingEntity, item, level
            )
        }

        override fun getEntity(event: EntityDamageByEntityEvent?): LivingEntity? {
            return if (event?.damager is LivingEntity) event.damager as LivingEntity else null
        }
    }

    val ENTITY_DAMAGE_DEFENSE = object : DataGather<EntityDamageByEntityEvent, CombatEnchant>() {
        override fun checkPriority(enchant: CombatEnchant, priority: EventPriority): Boolean {
            return enchant.protectPriority == priority
        }

        override fun getEnchantSlots(event: EntityDamageByEntityEvent): Array<EquipmentSlot> {
            return arrayOf(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
            )
        }

        override fun useEnchant(
            event: EntityDamageByEntityEvent, entity: LivingEntity, item: ItemStack, enchant: CombatEnchant, level: Int
        ): Boolean {
            if (event.cause == EntityDamageEvent.DamageCause.THORNS || event.cause == EntityDamageEvent.DamageCause.CUSTOM) return false
            return if (event.damager !is LivingEntity) false else enchant.onProtect(
                event, event.damager as LivingEntity, entity, item, level
            )
        }

        override fun getEntity(event: EntityDamageByEntityEvent?): LivingEntity? {
            return if (event?.entity is LivingEntity) event.entity as LivingEntity else null
        }
    }

    val ENTITY_KILL = object : DataGather<EntityDeathEvent, DeathEnchant>() {
        override fun checkPriority(enchant: DeathEnchant, priority: EventPriority): Boolean {
            return enchant.killPriority == priority
        }

        override fun getEnchantSlots(event: EntityDeathEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND)
        }

        override fun useEnchant(
            event: EntityDeathEvent, entity: LivingEntity, item: ItemStack, enchant: DeathEnchant, level: Int
        ): Boolean {
            return if (entity !is Player) false else enchant.onKill(event, event.entity, entity, item, level)
        }

        override fun getEntity(event: EntityDeathEvent?): LivingEntity? {
            return event?.entity?.killer
        }
    }

    val ENTITY_DEATH = object : DataGather<EntityDeathEvent, DeathEnchant>() {
        override fun checkPriority(enchant: DeathEnchant, priority: EventPriority): Boolean {
            return enchant.deathPriority == priority
        }

        override fun getEnchantSlots(event: EntityDeathEvent): Array<EquipmentSlot> {
            return EquipmentSlot.entries.toTypedArray()
        }

        override fun useEnchant(
            event: EntityDeathEvent, entity: LivingEntity, item: ItemStack, enchant: DeathEnchant, level: Int
        ): Boolean {
            return enchant.onDeath(event, entity, item, level)
        }

        override fun getEntity(event: EntityDeathEvent?): LivingEntity? {
            return event?.entity
        }
    }

    val FISHING = object : DataGather<PlayerFishEvent, FishingEnchant>() {
        override fun checkPriority(enchant: FishingEnchant, priority: EventPriority): Boolean {
            return enchant.fishingPriority == priority
        }

        override fun getEnchantSlots(event: PlayerFishEvent): Array<EquipmentSlot> {
            return arrayOf(EquipmentSlot.HAND)
        }

        override fun useEnchant(
            event: PlayerFishEvent, entity: LivingEntity, item: ItemStack, enchant: FishingEnchant, level: Int
        ): Boolean {
            return enchant.onFishing(event, item, level)
        }

        override fun getEntity(event: PlayerFishEvent?): LivingEntity? {
            return event?.player
        }
    }

    val INTERACT = object : DataGather<PlayerInteractEvent, InteractEnchant>() {
        override fun checkPriority(enchant: InteractEnchant, priority: EventPriority): Boolean {
            return enchant.interactPriority == priority
        }

        override fun getEnchantSlots(event: PlayerInteractEvent): Array<EquipmentSlot> {
            return if (event.hand == null) arrayOf(EquipmentSlot.HAND) else arrayOf(event.hand!!)
        }

        override fun useEnchant(
            event: PlayerInteractEvent, entity: LivingEntity, item: ItemStack, enchant: InteractEnchant, level: Int
        ): Boolean {
            return enchant.onInteract(event, entity, item, level)
        }

        override fun getEntity(event: PlayerInteractEvent?): LivingEntity? {
            return event?.player
        }
    }
}