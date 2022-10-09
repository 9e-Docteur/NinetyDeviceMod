package com.mrcrayfish.device.proxy;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.init.DeviceBlocks;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.network.task.MessageSyncApplications;
import com.mrcrayfish.device.network.task.MessageSyncConfig;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonProxy {
    List<AppInfo> allowedApps;
    int hashCode = -1;

    public void preInit() {

    }

    public void init() {
    }

    public void postInit() {
    }

    @Nullable
    public Application registerApplication(ResourceLocation identifier, Class<? extends Application> clazz) {
        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }
        if (SystemApplication.class.isAssignableFrom(clazz)) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }
        return null;
    }

    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        return true;
    }

    public boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getEntity().getLevel().isClientSide()) return;

        if (allowedApps != null) {
            PacketHandler.sendTo(new MessageSyncApplications(allowedApps), (ServerPlayer) event.getEntity());
        }
        PacketHandler.sendTo(new MessageSyncConfig(), (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level Level = event.getLevel();
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() == Items.PAPER) {
            if (Level.getBlockState(event.getPos()).getBlock() == DeviceBlocks.PRINTER.get()) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

    public void showNotification(CompoundTag tag) {
    }
}
