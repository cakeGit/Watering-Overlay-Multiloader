package com.cak.watering_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.cak.watering_overlay.WateringHighlightRenderer.renderWateringHighlightBox;

public class WateringHandlerEvents {
    
    public static void onClientTick() {
        WateringChecker.tickFarmlandDiscovery();
        WateringControls.tickControls();
    }
    
    public static void renderLevelLastEvent(PoseStack ps) {
        if (WateringOverlay.DisplayOptions.SELECTOR == OverlaySelector.OFF)
            return;
        
        Level level = Minecraft.getInstance().level;
        if (level != WateringChecker.lastLevel)
            return;
        
        //WateringChecker.FARMLAND_RANGE_BLOCKS will safely include WateringChecker.IMMEDIATE_RANGE_BLOCKS
    
        //Build rendering data for connected textures
        Map<BlockPos, WateredType> renderedTypeMap = new HashMap<>();
        
        for (BlockPos blockPos : WateringChecker.FARMLAND_RANGE_BLOCKS) {
            BlockState state = level.getBlockState(blockPos);
            
            if (WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInFarmlandRange(state))
                renderedTypeMap.put(blockPos, WateredType.FARMLAND);
            
            else if (WateringChecker.IMMEDIATE_HYDRATION_BLOCKS.contains(blockPos)
                && WateringOverlay.DisplayOptions.SELECTOR.shouldRenderInSugarCaneRange(state))
                renderedTypeMap.put(blockPos, WateredType.SUGAR_CANE_ONLY);
        }
        
        //Rendering oo la la
        for (Map.Entry<BlockPos, WateredType> entry : renderedTypeMap.entrySet()) {
            BlockPos blockPos = entry.getKey();
            WateredType wateredType = entry.getValue();
            
            EnumMap<Direction, Boolean> connectedSides = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values())
                connectedSides.put(direction, renderedTypeMap.containsKey(blockPos.relative(direction.getOpposite())));
            
            renderWateringHighlightBox(ps, blockPos, wateredType.getTexture(), connectedSides);
        }
    }
    
    
}
