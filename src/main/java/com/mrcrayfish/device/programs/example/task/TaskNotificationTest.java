package com.mrcrayfish.device.programs.example.task;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TaskNotificationTest extends Task
{
    public TaskNotificationTest()
    {
        super("notification_test");
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {

    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        Notification notification = new Notification(Icons.MAIL, "New Email!", "Check your inbox");
        notification.pushTo((ServerPlayer) player);

       /* MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<PlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(notification::pushTo);*/
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {

    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
