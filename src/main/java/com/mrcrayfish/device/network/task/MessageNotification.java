package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.network.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


/**
 * Author: MrCrayfish
 */
public class MessageNotification implements IPacket<MessageNotification>
{
    private CompoundTag notificationTag;

    public MessageNotification() {}

    public MessageNotification(CompoundTag compoundTag)
    {
        this.notificationTag = compoundTag;
    }

    public MessageNotification(Notification notification)
    {
        this(notification.toTag());
    }

    @Override
    public void encode(MessageNotification packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(packet.notificationTag);
    }

    @Override
    public MessageNotification decode(FriendlyByteBuf byteBuf) {
        CompoundTag notificationTag = byteBuf.readNbt();
        return new MessageNotification(notificationTag);
    }

    @Override
    public void handlePacket(MessageNotification packet, Supplier<NetworkEvent.Context> ctx) {
        MrCrayfishDeviceMod.proxy.showNotification(packet.notificationTag);
    }
}
