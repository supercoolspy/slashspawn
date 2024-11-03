package dev.spys.slashspawn.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import dev.spys.slashspawn.SlashSpawn
import dev.spys.slashspawn.SpawnManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

@CommandAlias("spawn|s")
class Spawn(val plugin: SlashSpawn): BaseCommand() {

    @Default
    fun spawn(player: Player) {
        val config = SpawnManager.config
        val spawn = config.spawn

        if (spawn == null) {
            player.sendMessage(config.noSpawn)
            return
        }

        {
            var time = config.time + 1

            val location = player.location

            player.sendMessage(MiniMessage.miniMessage().deserialize(config.spawnTeleportMessage.replace("%time%", (time - 1).toString(), true)))

            var task: BukkitTask? = null

            task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
                time--

                if (time <= 0) {
                    // Limitation: If spawn is changed while players are teleporting they will be teleported to the old spawn
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        player.teleportAsync(spawn)
                    })
                    task!!.cancel()
                    return@Runnable
                }

                if (config.cancelOnMove) {
                    val currentLocation = player.location

                    if (currentLocation.distanceSquared(location) > 0.5) {
                        player.sendMessage(config.moveCancel)
                        task!!.cancel()
                        return@Runnable
                    }
                }

                player.sendActionBar(MiniMessage.miniMessage().deserialize(config.spawnTeleportMessage.replace("%time%", time.toString(), true)))
            }, 0, 20)
        }.invoke()
    }

    @Subcommand("set")
    @CommandPermission("slashspawn.set")
    fun setSpawn(player: Player) {
        SpawnManager.changeLocation(plugin, player.location)
        player.sendMessage(Component.text("Changed spawn to your current location!", NamedTextColor.GREEN))
    }

    @Subcommand("unset")
    @CommandPermission("slashspawn.unset")
    fun unsetSpawn(sender: CommandSender) {
        SpawnManager.changeLocation(plugin, null)
        sender.sendMessage(Component.text("Unset Spawn!", NamedTextColor.GREEN))
    }

    @Subcommand("reload")
    @CommandPermission("slashspawn.reload")
    fun reload(sender: CommandSender) {
        SpawnManager.reload(plugin)
        sender.sendMessage(Component.text("Reloaded Config!", NamedTextColor.GREEN))
    }
}