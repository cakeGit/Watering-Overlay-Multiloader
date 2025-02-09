package com.cak.watering_overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumMap;

public class WateringHighlightRenderer {
    
    public static void renderWateringHighlightBox(PoseStack poseStack, BlockPos blockPos, ResourceLocation texture, EnumMap<Direction, Boolean> connectedSides) {
        AABB renderedCubeAABB = new AABB(
            new Vec3(0, 0, 0),
            new Vec3(1, 1, 1)
        );
        
        for (Direction direction : Direction.values()) {
            if (!connectedSides.get(direction))
                renderedCubeAABB = includeAABBs(renderedCubeAABB, cubeOnSide(direction.getOpposite().getNormal(), 1/16f, 1f));
        }

        VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.beaconBeam(texture, true));
        
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        
        poseStack.pushPose();
//
//        Quaternionf rotation = camera.rotation();
//        rotation.invert();
//        poseStack.mulPose(rotation);
////        poseStack.mulPose(new Quaternionf(camera.rotation()));
        translatePSVector(poseStack, camera.getPosition().scale(-1));
        
        translatePSVector(poseStack, Vec3.atLowerCornerOf(blockPos.offset(0, 0, 0)).multiply(1, 1, 1));
        renderHighlightCube(poseStack, buffer, renderedCubeAABB, connectedSides);
        poseStack.popPose();
        
    }
    
    private static void translatePSVector(PoseStack poseStack, Vec3 vec) {
        poseStack.translate((float) vec.x(), (float) vec.y(), (float) vec.z());
    }
    
    private static void renderHighlightCube(PoseStack poseStack, VertexConsumer buffer, AABB renderedCubeAABB, EnumMap<Direction, Boolean> connectedSides) {
        if (!connectedSides.get(Direction.NORTH))
            renderHighlightCubeSide(poseStack, buffer, Direction.NORTH, renderedCubeAABB,
                new Vec3(0, 0, 1),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.SOUTH))
            renderHighlightCubeSide(poseStack, buffer, Direction.SOUTH, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(1, 1, 0)
            );
        if (!connectedSides.get(Direction.WEST))
            renderHighlightCubeSide(poseStack, buffer, Direction.WEST, renderedCubeAABB,
                new Vec3(1, 0, 0),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.EAST))
            renderHighlightCubeSide(poseStack, buffer, Direction.EAST, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(0, 1, 1)
            );
        if (!connectedSides.get(Direction.UP))
            renderHighlightCubeSide(poseStack, buffer, Direction.UP, renderedCubeAABB,
                new Vec3(0, 1, 0),
                new Vec3(1, 1, 1)
            );
        if (!connectedSides.get(Direction.DOWN))
            renderHighlightCubeSide(poseStack, buffer, Direction.DOWN, renderedCubeAABB,
                new Vec3(0, 0, 0),
                new Vec3(1, 0, 1)
            );
    }
    
    private static void renderHighlightCubeSide(PoseStack poseStack, VertexConsumer buffer, Direction direction, AABB renderedCubeAABB, Vec3 from, Vec3 to) {
        Vec3 min = minVector(renderedCubeAABB).multiply(1, -1, 1).add(0, 1, 0);
        Vec3 max = maxVector(renderedCubeAABB).multiply(1, -1, 1).add(0, 1, 0);
        
        Vec3 diff = max.subtract(min);
        
        from = from.multiply(diff).add(min);
        to = to.multiply(diff).add(min);
        
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
        double normalOrdinal = sumVector(normal);
        Vec3 perpendiculars = new Vec3(1, 1, 1).subtract(normal.scale(normalOrdinal));
        
        Vec3 firstAxis = getFirstAxis(perpendiculars);
        Vec3 firstTo = from.add(diff.multiply(firstAxis));
        
        Vec3 secondaryAxis = getSecondaryAxis(perpendiculars);
        Vec3 secondaryTo = from.add(diff.multiply(secondaryAxis));
        
        Vec3 realDiff = to.subtract(from);
        Vec3 realDiffPrimary = realDiff.multiply(getFirstAxis(realDiff));
        Vec3 realDiffSecondary = realDiff.multiply(getSecondaryAxis(realDiff));

        float primaryUV = (float) ((sumVector(realDiffPrimary) / (1/16f)) * (4/256f));
        float secondaryUV = (float) ((sumVector(realDiffSecondary) / (1/16f)) * (4/256f));
        
        Vector3f n = new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
        
        Matrix4f m = poseStack.last().pose();
        
        int alpha = 90;
        
        if (normalOrdinal == -1)
            buffer.addVertex(m, (float) to.x, (float) to.y, (float) to.z)
                .setColor(255, 255, 255, alpha)
                .setUv(primaryUV, secondaryUV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setUv2(0, 0)
                .setNormal(n.x, n.y, n.z);
        else
            buffer.addVertex(m, (float) from.x, (float) from.y, (float) from.z)
                .setColor(255, 255, 255, alpha)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setUv2(0, 0)
                .setNormal(n.x, n.y, n.z);
        
        buffer.addVertex(m, (float) firstTo.x, (float) firstTo.y, (float) firstTo.z)
            .setColor(255, 255, 255, alpha)
            .setUv(0, secondaryUV)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(0, 0)
            .setNormal(n.x, n.y, n.z);
        
        if (normalOrdinal == 1)
            buffer.addVertex(m, (float) to.x, (float) to.y, (float) to.z)
                .setColor(255, 255, 255, alpha)
                .setUv(primaryUV, secondaryUV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setUv2(0, 0)
                .setNormal(n.x, n.y, n.z);
        else
            buffer.addVertex(m, (float) from.x, (float) from.y, (float) from.z)
                .setColor(255, 255, 255, alpha)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setUv2(0, 0)
                .setNormal(n.x, n.y, n.z);
        
        buffer.addVertex(m, (float) secondaryTo.x, (float) secondaryTo.y, (float) secondaryTo.z)
            .setColor(255, 255, 255, alpha)
            .setUv(primaryUV, 0)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setUv2(0, 0)
            .setNormal(n.x, n.y, n.z);
    }
    
    private static Vec3 getFirstAxis(Vec3 perpendiculars) {
        return perpendiculars.x != 0 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
    }
    
    private static Vec3 getSecondaryAxis(Vec3 perpendiculars) {
        return perpendiculars.z != 0 ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0);
    }
    
    private static AABB cubeOnSide(Vec3i normalVec3i, float height, float width) {
        Vec3 normal = Vec3.atLowerCornerOf(normalVec3i).multiply(1, -1, 1);
        
        double normalOrdinal = sumVector(normal); //Expect axis aligned so no need for anything else
        
        Vec3 faceCenter = new Vec3(0.5, 0.5, 0.5).add(normal.scale(0.5));
        
        Vec3 edgeOffset = new Vec3(1, 1, 1).subtract(normal.scale(normalOrdinal))
            .scale(0.5).scale(width);
        
        Vec3 min = faceCenter.subtract(edgeOffset);
        Vec3 max = faceCenter.add(edgeOffset).add(normal.scale(height));
        
        return new AABB(min, max);
    }
    
    private static double sumVector(Vec3 normal) {
        return normal.x() + normal.y() + normal.z();
    }
    
    private static Vec3 minVector(AABB AABB) {
        return new Vec3(AABB.minX, AABB.minY, AABB.minZ);
    }
    
    private static Vec3 maxVector(AABB AABB) {
        return new Vec3(AABB.maxX, AABB.maxY, AABB.maxZ);
    }
    
    private static AABB includeAABBs(AABB from, AABB to) {
        return new AABB(
            Math.min(from.minX, to.minX),
            Math.min(from.minY, to.minY),
            Math.min(from.minZ, to.minZ),
            Math.max(from.maxX, to.maxX),
            Math.max(from.maxY, to.maxY),
            Math.max(from.maxZ, to.maxZ)
        );
    }
    
}
