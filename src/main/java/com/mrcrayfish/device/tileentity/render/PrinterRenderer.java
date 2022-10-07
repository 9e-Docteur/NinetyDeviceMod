package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class PrinterRenderer implements BlockEntityRenderer<TileEntityPrinter> {
    private static final ModelPaper paperModel = new ModelPaper(Minecraft.getInstance().getEntityModels().bakeLayer(new ModelLayerLocation(new ResourceLocation("paper", "paper"), "paper")));

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
                    poseStack.mulPose(state.getValue(BlockPrinter.FACING).getRotation());
                    poseStack.mulPose(new Quaternion(1, 0, 0, 22.5f));
                    poseStack.translate(0, 0, 0.4);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    //paperModel.renderToBuffer(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
                if(te.isLoading())
                {
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.mulPose(state.getValue(BlockPrinter.FACING).getRotation());
                    poseStack.mulPose(new Quaternion(1, 0, 0, 22.5f));
                    double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (te.getRemainingPrintTime() - 10) / 20)));
                    poseStack.translate(0, progress, 0.36875);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    //paperModel.renderToBuffer(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                }
                else if(te.isPrinting())
                {
                    poseStack.translate(0.5, 0.078125, 0.5);
                    poseStack.mulPose(state.getValue(BlockPrinter.FACING).getRotation());
                    poseStack.mulPose(new Quaternion(1, 0, 0, 90f));
                    double progress = -0.35 + (0.50 * ((double) (te.getRemainingPrintTime() - 20) / te.getTotalPrintTime()));
                    poseStack.translate(0, progress, 0);
                    poseStack.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
                    //paperModel.renderToBuffer(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

                    poseStack.translate(0.3225, 0.085, -0.001);
                    poseStack.mulPose(new Quaternion(0, 1, 0, 180f));
                    poseStack.scale(0.3f, 0.3f, 0.3f);

                    IPrint print = te.getPrint();
                    if(print != null)
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(print.toTag());
                    }
                }
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
                RenderSystem.depthMask(false);
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(state.getValue(BlockPrinter.FACING).getRotation());
               // poseStack.mulPose(180F, 0, 1, 0);
                poseStack.translate(0.0675, 0.005, -0.032);
                poseStack.translate(-6.5 * 0.0625, -3.5 * 0.0625, 3.01 * 0.0625);
                poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
               // poseStack.glNormal3f(0.0F, 0.0F, -0.010416667F);
                poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(22.5f, 180, 0)));
                Minecraft.getInstance().font.draw(poseStack, Integer.toString(te.getPaperCount()), 0, 0, Color.WHITE.getRGB());
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

    public static class ModelPaper extends Model
    {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");

        private final ModelPart paperModel;

        public ModelPaper(ModelPart modelPart) {
            super(RenderType::entitySolid);
            this.paperModel = modelPart;
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("paper", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 22, 30, 1), PartPose.offset(0f, 0f, 0f));
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            this.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        private void render(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.paperModel.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }
}
