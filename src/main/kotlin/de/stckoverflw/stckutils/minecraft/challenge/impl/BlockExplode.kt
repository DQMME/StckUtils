package de.stckoverflw.stckutils.minecraft.challenge.impl

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.addComponent
import de.stckoverflw.stckutils.extension.coloredString
import de.stckoverflw.stckutils.extension.isPlaying
import de.stckoverflw.stckutils.extension.render
import de.stckoverflw.stckutils.minecraft.challenge.Challenge
import de.stckoverflw.stckutils.minecraft.challenge.nameKey
import de.stckoverflw.stckutils.util.GUIPage
import de.stckoverflw.stckutils.util.getGoBackItem
import de.stckoverflw.stckutils.util.placeHolderItemGray
import de.stckoverflw.stckutils.util.placeHolderItemWhite
import de.stckoverflw.stckutils.util.settingsGUI
import net.axay.kspigot.gui.ForInventoryFiveByNine
import net.axay.kspigot.gui.GUI
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.gui.rectTo
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import java.util.Locale

object BlockExplode : Challenge() {

    private var isFire: Boolean
        get() = Config.challengeConfig.getSetting(id, "isFire") as Boolean? ?: false
        set(value) = Config.challengeConfig.setSetting(id, "isFire", value)
    private var chance: Int
        get() = Config.challengeConfig.getSetting(id, "chance") as Int? ?: 50
        set(value) = Config.challengeConfig.setSetting(id, "chance", value)

    override val id: String = "block-explode"
    override val material: Material = Material.TNT
    override val usesEvents: Boolean = true

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine> = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        title = translatable(nameKey).coloredString(locale)
        defaultPage = 0

        page(0) {
            placeholder(Slots.Border, placeHolderItemGray)
            placeholder(Slots.RowTwoSlotTwo rectTo Slots.RowFourSlotEight, placeHolderItemWhite)

            button(
                Slots.RowThreeSlotOne,
                getGoBackItem(locale)
            ) {
                it.player.openGUI(settingsGUI(locale), GUIPage.challengesPageNumber)
            }

            button(
                Slots.RowThreeSlotFour,
                generateChanceItem(locale)
            ) {
                if (it.bukkitEvent.isLeftClick) {
                    if (chance <= 95) {
                        chance += 5
                    }
                } else if (it.bukkitEvent.isRightClick) {
                    if (chance >= 5) {
                        chance -= 5
                    }
                }
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateChanceItem(locale))
            }

            button(
                Slots.RowThreeSlotSix,
                generateFireItem(locale)
            ) {
                isFire = !isFire
                it.bukkitEvent.clickedInventory!!.setItem(it.bukkitEvent.slot, generateFireItem(locale))
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!event.player.isPlaying()) {
            return
        }
        if ((1..100).random() <= chance) {
            event.block.location.world.createExplosion(event.block.location, 3.3F, isFire)
        }
    }

    private fun generateChanceItem(locale: Locale) = itemStack(Material.SPRUCE_SIGN) {
        meta {
            name = translatable("$id.chance_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.chance_item.lore")
                        .args(text(chance))
                        .render(locale)
                )
            }
        }
    }

    private fun generateFireItem(locale: Locale) = itemStack(Material.FIRE_CHARGE) {
        meta {
            name = translatable("$id.fire_item.name")
                .render(locale)

            addLore {
                addComponent(
                    translatable("$id.fire_item.lore")
                        .args(
                            text(
                                isFire,
                                if (isFire) {
                                    TextColor.color(Color.GREEN.asRGB())
                                } else {
                                    TextColor.color(Color.RED.asRGB())
                                }
                            )
                        )
                        .render(locale)
                )
            }
        }
    }
}
