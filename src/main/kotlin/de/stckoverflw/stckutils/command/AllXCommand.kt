package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import de.stckoverflw.stckutils.minecraft.goal.impl.AllAdvancements
import de.stckoverflw.stckutils.minecraft.goal.impl.AllItems
import de.stckoverflw.stckutils.minecraft.goal.impl.AllMobs
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI

class AllXCommand {

    fun register() = command("allx", true) {
        runs {
            when (GoalManager.activeGoal) {
                is AllItems -> {
                    AllItems.resetFilter(player)
                    player.openGUI(AllItems.gui())
                }
                is AllMobs -> {
                    AllMobs.resetFilter(player)
                    player.openGUI(AllMobs.gui())
                }
                is AllAdvancements -> {
                    AllAdvancements.resetFilter(player)
                    player.openGUI(AllAdvancements.gui())
                }
                else -> player.sendMessage("§cAn AllX Goal needs to be enabled to do this")
            }
        }
    }
}
