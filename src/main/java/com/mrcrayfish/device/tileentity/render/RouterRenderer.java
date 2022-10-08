package com.mrcrayfish.device.tileentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.block.BlockRouter;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.tileentity.TileEntityOfficeChair;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * Author: MrCrayfish
 */
public class RouterRenderer implements BlockEntityRenderer<TileEntityRouter> {
    @Override
    public void render(TileEntityRouter te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        if(state.getBlock() != DeviceBlocks.ROUTER.get())
            return;

        if(te.isDebug())
        {
            poseStack.pushPose();
            {
                //RenderSystem.translate(x, y, z);
                Router router = te.getRouter();
                BlockPos routerPos = router.getPos();

                Vec3d linePositions = getLineStartPosition(state);
                final double startLineX = linePositions.x;
                final double startLineY = linePositions.y;
                final double startLineZ = linePositions.z;

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();

                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getInstance().level);
                DEVICES.forEach(networkDevice ->
                {
                    BlockPos devicePos = networkDevice.getPos();

                    RenderSystem.lineWidth(14F);
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(1f, 1f, 1f, 0.35f).endVertex();
                    tesselator.end();

                    RenderSystem.lineWidth(4F);
                    buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(0f, 1f, 0f, 0.5f).endVertex();
                    tesselator.end();
                });
            }
            poseStack.popPose();
        }
    }

    private Vec3d getLineStartPosition(BlockState state)
    {
        float lineX = 0.5F;
        float lineY = 0.1F;
        float lineZ = 0.5F;

        if (state.getValue(BlockRouter.VERTICAL)) {
            Quaternion rotation = state.getValue(BlockRouter.FACING).getRotation();
            rotation.mul(new Quaternion((float) (14 * 0.0625), 0.5f, (float) (14 * 0.0625), 0.5f));
            Vector3f fixedPosition = rotation.toXYZ();
            lineX = fixedPosition.x();
            lineY = 0.35f;
            lineZ = fixedPosition.z();
        }


        return new Vec3d(lineX, lineY, lineZ);
    }
}
