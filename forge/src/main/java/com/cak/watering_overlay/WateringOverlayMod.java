package com.cak.watering_overlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.cak.watering_overlay.WateringControls.TOGGLE_MODE;

@Mod(Constants.MOD_ID)
public class WateringOverlayMod {

    public WateringOverlayMod() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

    }
    
    
    @Mod.EventBusSubscriber(modid = WateringOverlay.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ControlRegistrationEvents {
        
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_MODE.get());
        }
        
    }
    
}