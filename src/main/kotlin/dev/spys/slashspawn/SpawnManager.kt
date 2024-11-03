package dev.spys.slashspawn

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration

object SpawnManager {
    private lateinit var fileConfig: FileConfiguration
    lateinit var config: SpawnConfig
        private set

    fun changeLocation(plugin: SlashSpawn, location: Location?) {
        config.spawn = location
        fileConfig.set("Spawn Location", location)
        plugin.saveConfig()
    }

    fun reload(plugin: SlashSpawn) {
        plugin.reloadConfig()

        load(plugin)
    }

    fun init(plugin: SlashSpawn) {
        plugin.saveDefaultConfig()

        load(plugin)
    }

    fun load(plugin: SlashSpawn) {
       fileConfig = plugin.config

        config = SpawnConfig(
            fileConfig.getString("Spawn Teleport Message")!!,
            MiniMessage.miniMessage().deserialize(fileConfig.getString("No Spawn Message")!!),
            MiniMessage.miniMessage().deserialize(fileConfig.getString("Spawn Cancellation Due To Movement Message")!!),
            fileConfig.getLocation("Spawn Location"),
            fileConfig.getInt("Teleport Time"),
            fileConfig.getBoolean("Cancel On Move")
        )
    }
}