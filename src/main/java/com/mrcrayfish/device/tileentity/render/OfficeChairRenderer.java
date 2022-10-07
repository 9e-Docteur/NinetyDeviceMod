package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.device.block.BlockOfficeChair;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class OfficeChairRenderer implements BlockEntityRenderer<TileEntityOfficeChair>
{
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(TileEntityOfficeChair te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BlockPos pos = te.getBlockPos();
        BlockState tempState = te.getLevel().getBlockState(pos);
        if(tempState.getBlock() != DeviceBlocks.CHAIR.get())
        {
            return;
        }

        poseStack.pushPose();
        {
            //poseStack.translate(x, y, z);

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, -/* te.getRotation()*/+180, 0)));
            poseStack.translate(-0.5, 0, -0.5);

            BlockState state = tempState.setValue(BlockOfficeChair.FACING, Direction.NORTH).setValue(BlockOfficeChair.TYPE, BlockOfficeChair.Type.SEAT);

//            poseStack.disableLighting();
//            poseStack.enableTexture2D();

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

//            Tessellator tessellator = Tessellator.getInstance();
//
//            BufferBuilder buffer = tessellator.getBuffer();
//            buffer.begin(7, DefaultVertexFormats.BLOCK);
//            buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
            blockrendererdispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);

//            buffer.setTranslation(0.0D, 0.0D, 0.0D);
//            tessellator.draw();

        }
        poseStack.popPose();
    }
}
