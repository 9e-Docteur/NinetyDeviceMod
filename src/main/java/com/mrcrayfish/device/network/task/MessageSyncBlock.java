package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.tileentity.TileEntityRouter;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Level.Level;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageSyncBlock implements IMessage, IMessageHandler<MessageSyncBlock, MessageSyncBlock>
{
    private BlockPos routerPos;

    public MessageSyncBlock() {}

    public MessageSyncBlock(BlockPos routerPos)
    {
        this.routerPos = routerPos;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(routerPos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        routerPos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public MessageSyncBlock onMessage(MessageSyncBlock message, MessageContext ctx)
    {
        Level Level = ctx.getServerHandler().player.Level;
        TileEntity tileEntity = Level.getBlockEntity(message.routerPos);
        if(tileEntity instanceof TileEntityRouter tileEntityRouter)
        {
            tileEntityRouter.syncDevicesToClient();
        }
        return null;
    }
}
