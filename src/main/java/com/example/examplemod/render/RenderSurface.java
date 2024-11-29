package com.example.examplemod.render;

import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Objects;

public abstract class RenderSurface {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/item/forcefield.png");

    public void render(Camera camera, PoseStack poseStack) {
        float camX = (float) camera.getPosition().x;
        float camY = (float) camera.getPosition().y;
        float camZ = (float) camera.getPosition().z;

        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);
        Matrix4f matrix4f = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(2.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        doRender(matrix4f, camX, camY, camZ);

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

    public abstract void doRender(Matrix4f posMatrix, float camX, float camY, float camZ);

    @Data
    @AllArgsConstructor
    public static class NorthAndSouthSide extends RenderSurface {

        private float fixedZ;
        private float minX;
        private float maxX;

        @Override
        public void doRender(Matrix4f posMatrix, float camX, float camY, float camZ) {
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            double effectiveRenderDistance = (Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
            float depthFar = Minecraft.getInstance().gameRenderer.getDepthFar();
            float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
            float f4 = (float)(-Mth.frac(camY * 0.5D));
            float f5 = f4 + depthFar;
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            if (camZ > fixedZ - effectiveRenderDistance && camZ < fixedZ + effectiveRenderDistance) {
                float f10 = (float)(Mth.floor(fixedZ) & 1) * 0.5F;
                for(float posX = minX; posX < maxX; f10 += 0.5F) {
                    float step = Math.min(1.0F, maxX - posX);
                    float f13 = step * 0.5F;
                    bufferbuilder.vertex(posMatrix, posX, -64, fixedZ).uv(f3 + f10, f3 + f5).endVertex();
                    bufferbuilder.vertex(posMatrix, posX + step, -64, fixedZ).uv(f3 + f13 + f10, f3 + f5).endVertex();
                    bufferbuilder.vertex(posMatrix, posX + step, 320, fixedZ).uv(f3 + f13 + f10, f3 + f4).endVertex();
                    bufferbuilder.vertex(posMatrix, posX, 320, fixedZ).uv(f3 + f10, f3 + f4).endVertex();
                    ++posX;
                }
            }
            BufferUploader.drawWithShader(bufferbuilder.end());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NorthAndSouthSide that)) return false;
            return Float.compare(fixedZ, that.fixedZ) == 0 && Float.compare(minX, that.minX) == 0 && Float.compare(maxX, that.maxX) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fixedZ, minX, maxX);
        }
    }

    @Data
    @AllArgsConstructor
    public static class EastAndWestSide extends RenderSurface {

        private float fixedX;
        private float minZ;
        private float maxZ;

        @Override
        public void doRender(Matrix4f posMatrix, float camX, float camY, float camZ) {
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            double effectiveRenderDistance = (Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
            float depthFar = Minecraft.getInstance().gameRenderer.getDepthFar();
            float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
            float f4 = (float)(-Mth.frac(camY * 0.5D));
            float f5 = f4 + depthFar;
            float f7 = (float)(Mth.floor(minZ) & 1) * 0.5F;
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            if (camX > fixedX - effectiveRenderDistance && camX < fixedX + effectiveRenderDistance) {
                for (float d7 = minZ; d7 < maxZ; f7 += 0.5F) {
                    float d8 = Math.min(1.0F, maxZ - d7);
                    float f8 = d8 * 0.5F;
                    bufferbuilder.vertex(posMatrix, fixedX, -64, d7).uv(f3 - f7, f3 + f5).endVertex();
                    bufferbuilder.vertex(posMatrix, fixedX, -64, d7 + d8).uv(f3 - (f8 + f7), f3 + f5).endVertex();
                    bufferbuilder.vertex(posMatrix, fixedX, 320, d7 + d8).uv(f3 - (f8 + f7), f3 + f4).endVertex();
                    bufferbuilder.vertex(posMatrix, fixedX, 320, d7).uv(f3 - f7, f3 + f4).endVertex();
                    ++d7;
                }
            }
            BufferUploader.drawWithShader(bufferbuilder.end());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EastAndWestSide that)) return false;
            return Float.compare(fixedX, that.fixedX) == 0 && Float.compare(minZ, that.minZ) == 0 && Float.compare(maxZ, that.maxZ) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fixedX, minZ, maxZ);
        }
    }
}
