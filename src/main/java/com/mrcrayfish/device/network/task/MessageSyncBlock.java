package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.network.IPacket;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncBlock implements IPacket<MessageSyncBlock>
{
    private BlockPos routerPos;

    public MessageSyncBlock() {}

    public MessageSyncBlock(BlockPos routerPos)
    {
        this.routerPos = routerPos;
    }

    @Override
    public void encode(MessageSyncBlock packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeBlockPos(routerPos);
    }

    @Override
    public MessageSyncBlock decode(FriendlyByteBuf byteBuf) {
        return new MessageSyncBlock(routerPos);
    }

    @Override
    public void handlePacket(MessageSyncBlock packet, Supplier<NetworkEvent.Context> ctx) {
        Level level = Objects.requireNonNull(ctx.get().getSender().level);
        BlockEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof TileEntityRouter router) {
            router.syncDevicesToClient();
        }
    }
}
