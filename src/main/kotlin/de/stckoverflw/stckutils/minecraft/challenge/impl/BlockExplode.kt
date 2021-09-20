package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.util.goBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object BlockExplode : Challenge() {

    private var isFire: Boolean
        get() = Config.challengeSettings.getSetting(id, "isFire") as Boolean? ?: false
        set(value) = Config.challengeSettings.setSetting(id, "isFire", value)
    private var chance: Int
        get() = Config.challengeSettings.getSetting(id, "chance") as Int? ?: 50
        set(value) = Config.challengeSettings.setSetting(id, "chance", value)

    override val id: String = "block-explode"
    override val name: String = "§cBlock Explode"
    override val material: Material = Material.TNT
    override val description: List<String> = listOf(
        " ",
        "§cEvery §7Block you break has a ",
        "§7chance to §cexplode",
    )
    override val usesEvents: Boolean = true

    override fun configurationGUI(): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        page(1) {
            // Placeholders at the Border of the Inventory
            placeholder(Slots.Border, placeHolderItemGray)
            // Placeholders in the Middle field of the Inventory
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            // Go back Item
            button(Slots.RowThreeSlotOne, goBackItem) { it.player.openGUI(settingsGUI(), 1) }

            button(Slots.RowThreeSlotFour, generateChanceItem()) {
                val player = it.player
                if (it.bukkitEvent.isLeftClick) {
                    if (chance < 100) {
                        chance += 5
                    } else {
                        player.sendMessage(StckUtilsPlugin.prefix + "§cYou reached the maximal value")
                    }
                } else if (it.bukkitEvent.isRightClick) {
                    if (chance > 5) {
                        chance -= 5
                    } else {
                        player.sendMessage(StckUtilsPlugin.prefix + "§cYou reached the minimal value")
                    }
                }
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateChanceItem())
            }

            button(Slots.RowThreeSlotSix, generateFireItem()) {
                isFire = !isFire
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateFireItem())
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) return
        if ((1..100).random() <= chance) {
            event.block.location.world.createExplosion(event.block.location, 3.3F, isFire)
        }
    }

    private fun generateChanceItem() = itemStack(Material.SPRUCE_SIGN) {
        meta {
            name = "§aChance"
            addLore {
                +" "
                +"§7The Chance how often the"
                +"§7Block should §cexplode"
                +" "
                +"§7Current value: §9$chance%"
            }
        }
    }

    private fun generateFireItem() = itemStack(Material.FIRE_CHARGE) {
        meta {
            name = "§cExplosion Fire"
            addLore {
                +" "
                +"§7If the Explosion should"
                +"§7Create Fire in the radius"
                +" "
                +"§7Current value: ".plus(if (isFire) "§a$isFire" else "§c$isFire")
            }
        }
    }
}
