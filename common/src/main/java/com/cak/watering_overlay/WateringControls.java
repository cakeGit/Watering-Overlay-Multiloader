package com.cak.watering_overlay;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class WateringControls {
    
    public static final Lazy<KeyMapping> TOGGLE_MODE = Lazy.lazy(() -> new KeyMapping(
        "key." + WateringOverlay.MODID + ".toggle_mode",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "key.categories." + WateringOverlay.MODID
    ));
    
    public static void tickControls() {
        
        if (Minecraft.getInstance().player == null) return;
        
        while (TOGGLE_MODE.get().consumeClick()) {
            WateringOverlay.DisplayOptions.SELECTOR_INDEX = (WateringOverlay.DisplayOptions.SELECTOR_INDEX + 1) % OverlaySelector.values().length;
            OverlaySelector newSelector = OverlaySelector.values()[WateringOverlay.DisplayOptions.SELECTOR_INDEX];
            WateringOverlay.DisplayOptions.SELECTOR = newSelector;
            
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("chat.watering_overlay.option_title").withStyle(ChatFormatting.GRAY).append(Component.literal("[").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)).append(Component.literal(newSelector.name()).withStyle(newSelector.getChatFormatting(), ChatFormatting.BOLD)).append(Component.literal("]").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)), true);
        }
    }
    
}
