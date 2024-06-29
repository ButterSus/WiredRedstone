package com.kneelawk.wiredredstone.mixin.impl;

import com.kneelawk.wiredredstone.keybinding.WRKeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int heldItemTooltipFade;
    @Shadow private ItemStack currentStack;
    @Final @Shadow private MinecraftClient client;
    @Shadow public abstract TextRenderer getTextRenderer();
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(at = @At("HEAD"), method = "renderHeldItemTooltip", cancellable = true)
    private void renderHeldItemTooltip(GuiGraphics graphics, CallbackInfo ci) {
        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }
            int i = this.getTextRenderer().getWidth(mutableText);
            int j = (this.scaledWidth - i) / 2;
            int k = this.scaledHeight - 59;
            assert this.client.interactionManager != null;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }

            int l = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                int var10001 = j - 2;
                int var10002 = k - 2;
                int var10003 = j + i + 2;
                Objects.requireNonNull(this.getTextRenderer());
                graphics.fill(var10001, var10002, var10003, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
                graphics.drawShadowedText(this.getTextRenderer(), mutableText, j, k - WRKeyBindings.getHeldItemTooltipOffset(), 16777215 + (l << 24));
            }
        }

        this.client.getProfiler().pop();
        ci.cancel();
    }
}
