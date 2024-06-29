package com.kneelawk.wiredredstone.keybinding

import com.kneelawk.wiredredstone.item.WRItems.BUNDLED_CABLES
import com.kneelawk.wiredredstone.item.WRItems.INSULATED_WIRES
import com.kneelawk.wiredredstone.item.WRItems.RED_ALLOY_WIRE
import com.kneelawk.wiredredstone.item.WRItems.STANDING_BUNDLED_CABLES
import com.kneelawk.wiredredstone.item.WRItems.STANDING_INSULATED_WIRES
import com.kneelawk.wiredredstone.item.WRItems.STANDING_RED_ALLOY_WIRE
import com.kneelawk.wiredredstone.mixin.api.MouseScrollListenerRegistry
import com.kneelawk.wiredredstone.net.WRNetworking
import com.mojang.blaze3d.platform.InputUtil
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBind
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import org.lwjgl.glfw.GLFW

object WRKeyBindings {
    private var hotbarScrollEnabled = true
    private var heldTooltipOffset = 0

    // Wires + Buses (toggle)
    private val mapWiresAndCables by lazy {
        sequenceOf(INSULATED_WIRES, STANDING_INSULATED_WIRES, BUNDLED_CABLES, STANDING_BUNDLED_CABLES)
            .plus(mapOf(null to RED_ALLOY_WIRE))
            .plus(mapOf(null to STANDING_RED_ALLOY_WIRE))
            .map { it.entries }.reduce { a, b -> a + b }
            .associate {
                it.value to sequenceOf(
                    INSULATED_WIRES[it.key] ?: RED_ALLOY_WIRE,
                    STANDING_INSULATED_WIRES[it.key] ?: STANDING_RED_ALLOY_WIRE,
                    BUNDLED_CABLES[it.key]!!,
                    STANDING_BUNDLED_CABLES[it.key]!!
                )
            }
    }

    // Keybindings
    val TOGGLE_WIRE_STATE by lazy {
        KeyBind(
            "key.wiredredstone.toggle_wire_state",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.wiredredstone.mod_name"
        )
    }

    fun init() {
        KeyBindingHelper.registerKeyBinding(TOGGLE_WIRE_STATE)

        // Scroll menu
        MouseScrollListenerRegistry.registerListener { _, _, scrollDeltaY ->
            if (hotbarScrollEnabled) return@registerListener

            val client = MinecraftClient.getInstance()
            val player = client.player ?: return@registerListener
            val activeStack = player.mainHandStack
            val wiresAndCables = mapWiresAndCables[activeStack.item]?.toList()

            if (TOGGLE_WIRE_STATE.isPressed && wiresAndCables != null) {
                val menuIndex = wiresAndCables.indexOf(activeStack.item)
                var newIndex = menuIndex - scrollDeltaY.toInt()
                if (newIndex < 0) newIndex = wiresAndCables.size - 1
                if (newIndex >= wiresAndCables.size) newIndex = 0
                val newItem = wiresAndCables[newIndex]

                val newStack = ItemStack(newItem, activeStack.count)
                newStack.nbt = activeStack.nbt

                val buf = PacketByteBuf(Unpooled.buffer())
                buf.writeInt(player.inventory.selectedSlot)
                buf.writeItemStack(newStack)

                ClientPlayNetworking.send(WRNetworking.INVENTORY_UPDATE_CHANNEL, buf)
            }
        }

        // Render select list over hotbar
        HudRenderCallback.EVENT.register { guiGraphics, _ ->
            val player = MinecraftClient.getInstance().player ?: return@register
            if (!player.isCreative) return@register
            val activeItem = player.mainHandStack.item
            val activeSlot = player.inventory.selectedSlot
            val wiresAndCables = mapWiresAndCables[activeItem]?.toList()

            if (TOGGLE_WIRE_STATE.isPressed && wiresAndCables != null) {
                hotbarScrollEnabled = false
                val menuIndex = wiresAndCables.indexOf(activeItem)

                val hotbarX = guiGraphics.scaledWindowWidth / 2 - 91
                val hotbarY = guiGraphics.scaledWindowHeight - 22

                heldTooltipOffset = 12

                wiresAndCables.forEachIndexed { index, item ->
                    val itemStack = ItemStack(item)
                    val x = hotbarX + (index + activeSlot - menuIndex) * 20 + 3
                    val y = hotbarY - 20

                    guiGraphics.drawItem(itemStack, x, y)
                    if (index == menuIndex) guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, -2130706433)
                }
            } else {
                hotbarScrollEnabled = true
                heldTooltipOffset = 0
            }
        }
    }

    @JvmStatic
    fun isHotbarScrollEnabled(): Boolean {
        return hotbarScrollEnabled
    }

    @JvmStatic
    fun getHeldItemTooltipOffset(): Int {
        return heldTooltipOffset
    }
}