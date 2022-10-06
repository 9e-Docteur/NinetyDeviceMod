package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Notification;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageNotification implements IMessage, IMessageHandler<MessageNotification, IMessage>
{
    private CompoundTag notificationTag;

    public MessageNotification() {}

    public MessageNotification(Notification notification)
    {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, notificationTag);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        notificationTag = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageNotification message, MessageContext ctx)
    {
        MrCrayfishDeviceMod.proxy.showNotification(message.notificationTag);
        return null;
    }
}
