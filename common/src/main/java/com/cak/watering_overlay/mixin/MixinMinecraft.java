package com.cak.watering_overlay.mixin;

import com.cak.watering_overlay.Constants;
import com.cak.watering_overlay.WateringHandlerEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(at = @At("HEAD"), method = "tick")
    private void init(CallbackInfo info) {
        WateringHandlerEvents.onClientTick();
    }
}