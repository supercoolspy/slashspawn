package dev.spys.slashspawn

import co.aikar.commands.PaperCommandManager
import dev.spys.slashspawn.commands.Spawn
import org.bukkit.plugin.java.JavaPlugin

class SlashSpawn : JavaPlugin() {

    override fun onEnable() {
        SpawnManager.init(this)

        val commandManager = PaperCommandManager(this)
        commandManager.registerCommand(Spawn(this))
    }

    override fun onDisable() {}
}
