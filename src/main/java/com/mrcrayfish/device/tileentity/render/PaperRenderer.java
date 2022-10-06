package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPaper;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityPaper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class PaperRenderer implements BlockEntityRenderer<TileEntityPaper> {
    @Override
    public void render(TileEntityPaper te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        {
           //poseStack.translate(x, y, z);
            poseStack.translate(0.5, 0.5, 0.5);
            BlockState state = te.getLevel().getBlockState(te.getBlockPos());
            if(state.getBlock() != DeviceBlocks.PAPER.get()) return;
            poseStack.mulPose(state.getValue(BlockPaper.FACING).getHorizontalIndex() * -90F + 180F, 0, 1, 0);
            poseStack.mulPose(new Quaternion(0, 0, 1, -te.getRotation()));
            poseStack.translate(-0.5, -0.5, -0.5);

            IPrint print = te.getPrint();
            if(print != null)
            {
                CompoundTag data = print.toTag();
                if(data.contains("pixels", Tag.TAG_INT_ARRAY) && data.contains("resolution", Tag.TAG_INT))
                {
                    Minecraft.getInstance().getTextureManager().bindForSetup(PrinterRenderer.ModelPaper.TEXTURE);
                    if(DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBoolean("cut"))
                    {
                        drawCuboid(0, 0, 0, 16, 16, 1, bufferSource);
                    }

                    poseStack.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                    poseStack.pushPose();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(data);
                    }
                    poseStack.popPose();

                    poseStack.pushPose();
                    {
                        if(DeviceConfig.RENDER_PRINTED_3D.get() && data.getBoolean("cut"))
                        {
                            CompoundTag tag = print.toTag();
                            drawPixels(poseStack, tag.getIntArray("pixels"), tag.getInt("resolution"), tag.getBoolean("cut"), packedLight, bufferSource);
                        }
                    }
                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();
    }

    private static void drawCuboid(double x, double y, double z, double width, double height, double depth, MultiBufferSource bufferSource)
    {
        x /= 16;
        y /= 16;
        z /= 16;
        width /= 16;
        height /= 16;
        depth /= 16;
//        RenderSystem.disableLighting();
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.glNormal3f(0.0F, 1.0F, 0.0F);
        double v = x + width + 1 - (width + width);
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH, bufferSource);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST, bufferSource);
        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST, bufferSource);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN, bufferSource);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP, bufferSource);
//        RenderSystem.disableRescaleNormal();
//        RenderSystem.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction, MultiBufferSource bufferSource) {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

    }

    private static long AA = 0;
    private static void drawPixels(PoseStack poseStack, int[] pixels, int resolution, boolean cut, int packedLight, MultiBufferSource bufferSource) {
        double scale = 16 / (double) resolution;
        var d = new DynamicTexture(resolution, resolution, true);
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {

                int r = (pixels[j + i * resolution] >> 16 & 255);
                int g = (pixels[j + i * resolution] >> 8 & 255);
                int b = (pixels[j + i * resolution] & 255);
                int a = (int) Math.floor((pixels[j + i * resolution] >> 24 & 255));
                assert d.getPixels() != null;
                d.getPixels().setPixelRGBA(i, j, new Color(r, g, b, a).getRGB());
            }
        }
        ResourceLocation resourcelocation = Minecraft.getInstance().getTextureManager().register("map/" + AA, d);
        Matrix4f matrix4f = poseStack.last().pose();
        var vertexconsumer = bufferSource.getBuffer(RenderType.text(resourcelocation));
        vertexconsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(packedLight).endVertex();
        AA++;
    }
}
