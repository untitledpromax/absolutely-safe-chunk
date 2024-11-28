package com.example.examplemod.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix4f;

@Slf4j
public class ChunkRegionRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/item/forcefield.png");

    public static void renderChunkRegion(Camera camera, PoseStack poseStack, ChunkPos chunkPos) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        float camX = (float) camera.getPosition().x;
        float camY = (float) camera.getPosition().y;
        float camZ = (float) camera.getPosition().z;
        float depthFar = Minecraft.getInstance().gameRenderer.getDepthFar();
        double effectiveRenderDistance = (Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);

        Matrix4f matrix4f = poseStack.last().pose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(2.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();
        float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
        float f4 = (float)(-Mth.frac(camera.getPosition().y * 0.5D));
        float f5 = f4 + depthFar;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float minZ = chunkPos.getMinBlockZ();
        float maxZ = chunkPos.getMaxBlockZ() + 1;
        float minX = chunkPos.getMinBlockX();
        float maxX = chunkPos.getMaxBlockX() + 1;
        float f7 = (float)(Mth.floor(minZ) & 1) * 0.5F;
        if (camX > chunkPos.getMaxBlockX() - effectiveRenderDistance) {
            for (float d7 = minZ; d7 < maxZ; f7 += 0.5F) {
                float d8 = Math.min(1.0F, maxZ - d7);
                float f8 = d8 * 0.5F;
                bufferbuilder.vertex(matrix4f, chunkPos.getMaxBlockX() + 1, -64, d7).uv(f3 - f7, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMaxBlockX() + 1, -64, d7 + d8).uv(f3 - (f8 + f7), f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMaxBlockX() + 1, 320, d7 + d8).uv(f3 - (f8 + f7), f3 + f4).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMaxBlockX() + 1, 320, d7).uv(f3 - f7, f3 + f4).endVertex();
                ++d7;
            }
        }
        float f6 = (float)(Mth.floor(minZ) & 1) * 0.5F;
        if (camX < chunkPos.getMinBlockX() + effectiveRenderDistance) {
            float f9 = f6;
            for(float d9 = minZ; d9 < maxZ; f9 += 0.5F) {
                float d12 = Math.min(1.0F, maxZ - d9);
                float f12 = d12 * 0.5F;
                bufferbuilder.vertex(matrix4f, chunkPos.getMinBlockX(), -64, d9).uv(f3 + f9, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMinBlockX(), -64, d9 + d12).uv(f3 + f12 + f9, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMinBlockX(), 320, d9 + d12).uv(f3 + f12 + f9, f3 + f4).endVertex();
                bufferbuilder.vertex(matrix4f, chunkPos.getMinBlockX(), 320, d9).uv(f3 + f9, f3 + f4).endVertex();
                ++d9;
            }
        }

        if (camZ > chunkPos.getMaxBlockZ() - effectiveRenderDistance) {
            float f10 = f6;
            for(float d10 = minX; d10 < maxX; f10 += 0.5F) {
                float d13 = Math.min(1.0F, maxX - d10);
                float f13 = d13 * 0.5F;
                bufferbuilder.vertex(matrix4f, d10, -64, chunkPos.getMaxBlockZ() + 1).uv(f3 + f10, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, d10 + d13, -64, chunkPos.getMaxBlockZ() + 1).uv(f3 + f13 + f10, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, d10 + d13, 320, chunkPos.getMaxBlockZ() + 1).uv(f3 + f13 + f10, f3 + f4).endVertex();
                bufferbuilder.vertex(matrix4f, d10, 320, chunkPos.getMaxBlockZ() + 1).uv(f3 + f10, f3 + f4).endVertex();
                ++d10;
            }
        }

        if (camZ < chunkPos.getMinBlockZ() + effectiveRenderDistance) {
            float f11 = f6;
            for(float d11 = minX; d11 < maxX; f11 += 0.5F) {
                float d14 = Math.min(1.0F, maxX - d11);
                float f14 = d14 * 0.5F;
                bufferbuilder.vertex(matrix4f, d11, -64, chunkPos.getMinBlockZ()).uv(f3 - f11, f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, d11 + d14, -64, chunkPos.getMinBlockZ()).uv(f3 - (f14 + f11), f3 + f5).endVertex();
                bufferbuilder.vertex(matrix4f, d11 + d14, 320, chunkPos.getMinBlockZ()).uv(f3 - (f14 + f11), f3 + f4).endVertex();
                bufferbuilder.vertex(matrix4f, d11, 320, chunkPos.getMinBlockZ()).uv(f3 - f11, f3 + f4).endVertex();
                ++d11;
            }
        }

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

}
