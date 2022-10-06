package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncConfig implements IPacket<MessageSyncConfig>
{
    public MessageSyncConfig() {

    }
    public MessageSyncConfig(FriendlyByteBuf friendlyByteBuf){
        DeviceConfig.readSyncTag(Objects.requireNonNull(friendlyByteBuf.readNbt()));
    }

    @Override
    public void encode(MessageSyncConfig packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public MessageSyncConfig decode(FriendlyByteBuf byteBuf) {
        return null;
    }

    @Override
    public void handlePacket(MessageSyncConfig packet, Supplier<NetworkEvent.Context> ctx) {

    }
}
