package me.duro.redenchants.npcs

import me.duro.redenchants.RedEnchants
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.trait.TraitInfo
import net.citizensnpcs.trait.LookClose
import org.bukkit.entity.EntityType

class NPCRegistry {
    private val npcTraits = listOf(
        Enchanter(),
        HeadHunter(),
        SoulMerchant(),
    )

    private val npcNames = listOf(
        "Enchanter",
        "Soul Merchant",
        "Head Hunter",
    )

    fun register() {
        CitizensAPI.getTraitFactory().apply {
            npcTraits.forEach { registerTrait(TraitInfo.create(it.javaClass).withName(it.name)) }
        }

        CitizensAPI.getNPCRegistry().filter { npcNames.contains(it.name) }.forEach { it.destroy() }

        CitizensAPI.getNPCRegistry().createNPC(
            EntityType.WITCH, "&c&lEnchanter", RedEnchants.instance.config.data.npcs.enchanterLocation
        ).apply {
            addTrait(Enchanter())
            getOrAddTrait(LookClose::class.java).lookClose(true)
        }

        CitizensAPI.getNPCRegistry().createNPC(
            EntityType.WITHER_SKELETON, "&c&lSoul Merchant", RedEnchants.instance.config.data.npcs.soulMerchantLocation
        ).apply {
            addTrait(SoulMerchant())
            getOrAddTrait(LookClose::class.java).lookClose(true)
        }

        CitizensAPI.getNPCRegistry().createNPC(
            EntityType.SLIME, "&c&lHead Hunter", RedEnchants.instance.config.data.npcs.headHunterLocation
        ).apply {
            addTrait(HeadHunter())
            getOrAddTrait(LookClose::class.java).lookClose(true)
        }
    }

    fun unregister() {
        CitizensAPI.getTraitFactory().apply {
            npcTraits.forEach { deregisterTrait(TraitInfo.create(it.javaClass).withName(it.name)) }
        }
    }
}