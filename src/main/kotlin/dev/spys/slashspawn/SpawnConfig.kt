package dev.spys.slashspawn

import net.kyori.adventure.text.Component
import org.bukkit.Location

data class SpawnConfig(
    val spawnTeleportMessage: String,
    val noSpawn: Component,
    val moveCancel: Component,
    var spawn: Location?,
    val time: Int,
    val cancelOnMove: Boolean
)