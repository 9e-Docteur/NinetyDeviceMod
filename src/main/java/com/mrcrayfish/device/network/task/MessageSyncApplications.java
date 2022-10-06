package com.mrcrayfish.device.network.task;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.proxy.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MessageSyncApplications implements PacketListener, Packet<MessageSyncApplications> {
    private List<AppInfo> allowedApps;

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
    public void write(FriendlyByteBuf p_131343_) {
        p_131343_.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            p_131343_.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public void handle(MessageSyncApplications p_131342_) {
        //Devices.setAllowedApps(allowedApps);
    }

    @Override
    public void onDisconnect(Component p_130552_) {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
