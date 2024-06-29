package com.kneelawk.wiredredstone.mixin.impl;

import com.kneelawk.wiredredstone.mixin.api.MouseScrollListenerRegistry;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method = "onMouseScroll")
    private void onMouseScroll(long window, double scrollDeltaX, double scrollDeltaY, CallbackInfo ci) {
        MouseScrollListenerRegistry.notifyListeners(window, scrollDeltaX, scrollDeltaY);
    }
}
