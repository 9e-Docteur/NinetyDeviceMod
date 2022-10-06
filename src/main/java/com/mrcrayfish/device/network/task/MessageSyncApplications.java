package com.mrcrayfish.device.network.task;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.network.IPacket;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.proxy.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageSyncApplications implements IPacket<MessageSyncApplications> {
    private List<AppInfo> allowedApps;

    public MessageSyncApplications(){

    }

    public MessageSyncApplications(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(appId);
            if (info != null) {
                builder.add(info);
            } else {
                //Devices.LOGGER.error("Missing application '" + appId + "'");
            }
        }

        allowedApps = builder.build();
    }

    public MessageSyncApplications(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }


    @Override
    public void encode(MessageSyncApplications packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            byteBuf.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public MessageSyncApplications decode(FriendlyByteBuf byteBuf) {
        return null;
    }

    @Override
    public void handlePacket(MessageSyncApplications packet, Supplier<NetworkEvent.Context> ctx) {
        MrCrayfishDeviceMod.setAllowedApps(allowedApps);
    }
}
