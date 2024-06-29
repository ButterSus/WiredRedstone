package com.kneelawk.wiredredstone.mixin.impl;

import com.kneelawk.wiredredstone.keybinding.WRKeyBindings;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyArg(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawShadowedText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"), index = 3)
    private int modifyHeldItemTooltipOffset(int offset) {
        return offset - WRKeyBindings.getHeldItemTooltipOffset();
    }
}
