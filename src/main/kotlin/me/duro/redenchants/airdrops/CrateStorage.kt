package me.duro.redenchants.airdrops

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import me.duro.redenchants.RedEnchants
import me.duro.redenchants.enchants.impls.RedEnchantRarity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption


class CrateStorage {
    private val crateFile = File(RedEnchants.instance.dataFolder, "crates.json")
    private var crateData = mutableMapOf<RedEnchantRarity, String>()

    fun loadCrates(): CrateStorage {
        if (!crateFile.exists()) RedEnchants.instance.saveResource("crates.json", false)

        val map = RedEnchants.instance.gson.fromJson(crateFile.readText(), HashMap::class.java)
        val data = RedEnchants.instance.gson.toJson(map)

        crateData = RedEnchants.instance.gson.fromJson(
            data,
            object : TypeToken<MutableMap<RedEnchantRarity, String>>() {}.type
        )

        return this
    }

    private fun saveCrates() {
        val json = RedEnchants.instance.gson.toJson(crateData)
        crateFile.delete()
        Files.write(crateFile.toPath(), json.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    fun getCrate(rarity: RedEnchantRarity): List<ItemStack?> {
        return crateData[rarity]?.let { itemStacksFromString(it).toList().filterNotNull() } ?: emptyList()
    }

    fun setCrate(rarity: RedEnchantRarity, inventory: Inventory) {
        crateData[rarity] = inventoryToString(inventory)

        saveCrates()
    }

    @Throws(java.lang.IllegalStateException::class)
    private fun inventoryToString(inventory: Inventory): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)

            dataOutput.writeInt(inventory.size)

            for (i in 0 until inventory.size) {
                dataOutput.writeObject(inventory.getItem(i))
            }

            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: java.lang.Exception) {
            throw java.lang.IllegalStateException("Unable to save item stacks.", e)
        }
    }

    @Throws(IOException::class)
    private fun itemStacksFromString(data: String): Array<ItemStack?> {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())

            for (i in items.indices) {
                items[i] = dataInput.readObject() as? ItemStack
                if (items[i] == null) continue
            }

            dataInput.close()
            items
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }
}
