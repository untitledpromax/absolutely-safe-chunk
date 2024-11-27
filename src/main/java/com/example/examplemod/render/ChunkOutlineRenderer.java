package com.example.examplemod.render;

import com.example.examplemod.blocks.TestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Slf4j
@Mod.EventBusSubscriber
public class ChunkOutlineRenderer {

    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderLevelStageEvent event) {
        // 确保在最后阶段渲染
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS || TestBlockEntity.getChunkPosCntMap() == null) {
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;
        TestBlockEntity.getChunkPosCntMap().forEach((chunkPos, cnt) -> {
            AABB boundingBox = new AABB(chunkPos.getMinBlockX(), -64, chunkPos.getMinBlockZ(),
                    chunkPos.getMaxBlockX(), 320, chunkPos.getMaxBlockZ());
            renderChunkOutline(poseStack, bufferSource, boundingBox, camX, camY, camZ);
        });
//        int chunkX = 10;
//        int chunkZ = 20;
//        AABB boundingBox = new AABB(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 16, 256, (chunkZ << 4) + 16);
//
//        renderChunkOutline(poseStack, bufferSource, boundingBox, camX, camY, camZ);
    }

    private static void renderChunkOutline(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, AABB boundingBox, double camX, double camY, double camZ) {
        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);

        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
                boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }
}
