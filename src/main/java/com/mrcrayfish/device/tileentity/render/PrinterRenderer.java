package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class PrinterRenderer implements BlockEntityRenderer<TileEntityPrinter> {
    private static final ModelPaper MODEL_PAPER = new ModelPaper();

    @Override
    public void render(TileEntityPrinter te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        if(state.getBlock() != DeviceBlocks.PRINTER.get())
            return;

        poseStack.pushPose();
        {
            //RenderSystem.setShaderTexture(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderSystem.translate(x, y, z);

            if(te.hasPaper())
            {
                poseStack.pushPose();
                {
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    poseStack.rotate(22.5F, 1, 0, 0);
                    poseStack.translate(0, 0, 0.4);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
                if(te.isLoading())
                {
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    poseStack.rotate(22.5F, 1, 0, 0);
                    double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (te.getRemainingPrintTime() - 10) / 20)));
                    poseStack.translate(0, progress, 0.36875);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);
                }
                else if(te.isPrinting())
                {
                    poseStack.translate(0.5, 0.078125, 0.5);
                    poseStack.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                    poseStack.rotate(90F, 1, 0, 0);
                    double progress = -0.35 + (0.50 * ((double) (te.getRemainingPrintTime() - 20) / te.getTotalPrintTime()));
                    poseStack.translate(0, progress, 0);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    MODEL_PAPER.render(null, 0F, 0F, 0F, 0F, 0F, 0.015625F);

                    poseStack.translate(0.3225, 0.085, -0.001);
                    poseStack.rotate(180F, 0, 1, 0);
                    poseStack.scale(0.3, 0.3, 0.3);

                    IPrint print = te.getPrint();
                    if(print != null)
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(print.toTag());
                    }
                }
            }
            poseStack.popMatrix();

            poseStack.pushMatrix();
            {
                RenderSystem.depthMask(false);
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.rotate(state.getValue(BlockPrinter.FACING).getHorizontalIndex() * -90F, 0, 1, 0);
                poseStack.rotate(180F, 0, 1, 0);
                poseStack.translate(0.0675, 0.005, -0.032);
                poseStack.translate(-6.5 * 0.0625, -3.5 * 0.0625, 3.01 * 0.0625);
                poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
                poseStack.glNormal3f(0.0F, 0.0F, -0.010416667F);
                poseStack.rotate(22.5F, 1, 0, 0);
                Minecraft.getMinecraft().fontRenderer.drawString(Integer.toString(te.getPaperCount()), 0, 0, Color.WHITE.getRGB());
                //RenderSystem.setShaderTexture(1F, 1.0F, 1.0F, 1.0F);
                RenderSystem.depthMask(true);
            }
            poseStack.popPose();
        }
        poseStack.popPose();

        poseStack.pushPose();
        {
            poseStack.translate(0, -0.5, 0);
            //super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        }
        poseStack.pushPose();
    }

    public static class ModelPaper extends ModelBase
    {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");

        private final ModelRenderer box = new ModelRenderer(this, 0, 0).addBox(0, 0, 0, 22, 30, 1);

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            box.render(scale);
        }
    }
}
