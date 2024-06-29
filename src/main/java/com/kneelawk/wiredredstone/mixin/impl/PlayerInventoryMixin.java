package com.kneelawk.wiredredstone.mixin.impl;

import com.kneelawk.wiredredstone.keybinding.WRKeyBindings;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(at = @At("HEAD"), method = "scrollInHotbar(D)V", cancellable = true)
    private void disableScrolling(double scrollAmount, CallbackInfo callbackInfo) {
        if (!WRKeyBindings.isHotbarScrollEnabled()) {
            callbackInfo.cancel();
        }
    }
}
