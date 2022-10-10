package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static io.netty.util.ResourceLeakDetector.getLevel;

public class LaptopRenderer implements BlockEntityRenderer<TileEntityLaptop>
{
	private final Minecraft mc = Minecraft.getInstance();

	//private final ItemEntity entityItem;


	public LaptopRenderer(BlockEntityRendererProvider.Context pContextd) {
		//entityItem = new ItemEntity(Minecraft.getInstance().level, 0D, 0D, 0D, ItemStack.EMPTY);
	}

	@Override
	public void render(TileEntityLaptop te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
	{
		BlockState state = DeviceBlocks.getAllLaptops().get(0).defaultBlockState().setValue(BlockLaptop.TYPE, BlockLaptop.Type.SCREEN);
		BlockPos pos = te.getBlockPos();
		
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		poseStack.pushPose();
		{
			//poseStack.translate(x, y, z);

			if(te.isExternalDriveAttached())
			{
				poseStack.pushPose();
				{
					poseStack.translate(0.5, 0, 0.5);
					poseStack.mulPose(te.getBlockState().getValue(BlockLaptop.FACING).getRotation());
					poseStack.translate(-0.5, 0, -0.5);
					poseStack.translate(0.595, -0.2075, -0.005);
					//entityItem.flyDist = 0.0F;
					//entityItem.setItem(new ItemStack(DeviceItems.FLASH_DRIVE.get(), 1, te.getExternalDriveColor()));
					//Minecraft.getInstance().levelRenderer.renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, poseStack, bufferSource);
					poseStack.translate(0.1, 0, 0);
				}
				poseStack.popPose();
			}

			poseStack.pushPose();
			{
				var direction = te.getBlockState().getValue(BlockLaptop.FACING).getClockWise().toYRot();
				poseStack.translate(0.5, 0, 0.5);
				poseStack.mulPose(Vector3f.YP.rotationDegrees(te.getBlockState().getValue(BlockLaptop.FACING) == Direction.EAST || te.getBlockState().getValue(BlockLaptop.FACING) == Direction.WEST ? direction + 90 : direction - 90));
				poseStack.translate(-0.5, 0, -0.5);
				poseStack.translate(0, 0.0625, 0.25);
				poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(te.getScreenAngle(partialTick) + 180, 0, 0)));

//				poseStack.disableLighting();
//				Tesselator tessellator = Tesselator.getInstance();
//				BufferBuilder buffer = tessellator.getBuilder();
//				buffer.begin(7, DefaultVertexFormats.BLOCK);
//				buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());

				BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
				BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
				//blockrendererdispatcher.getModelRenderer().renderModel(getLevel(), ibakedmodel, state, pos, bufferSource, false);
				poseStack.pushPose();
				blockrendererdispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);
				poseStack.popPose();

//				buffer.setTranslation(0.0D, 0.0D, 0.0D);
//				tessellator.draw();
//				RenderSystem.enableLighting();
			}
			poseStack.popPose();
		 }
		poseStack.popPose();
	}
}
