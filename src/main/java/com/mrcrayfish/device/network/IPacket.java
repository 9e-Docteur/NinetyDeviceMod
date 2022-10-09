package com.mrcrayfish.device.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
* Author: Josia
*/
public interface IPacket<T> {

    void encode(T packet, FriendlyByteBuf byteBuf);

    T decode(FriendlyByteBuf byteBuf);

    void handlePacket(T packet, Supplier<NetworkEvent.Context> ctx);

    default void handle(T packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handlePacket(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
