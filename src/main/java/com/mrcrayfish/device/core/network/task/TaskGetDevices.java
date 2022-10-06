package com.mrcrayfish.device.core.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;

/**
 * Author: MrCrayfish
 */
public class TaskGetDevices extends Task
{
    private BlockPos devicePos;
    private Class<? extends TileEntityNetworkDevice> targetDeviceClass;

    private Collection<NetworkDevice> foundDevices;

    private TaskGetDevices()
    {
        super("get_network_devices");
    }

    public TaskGetDevices(BlockPos devicePos)
    {
        this();
        this.devicePos = devicePos;
    }

    public TaskGetDevices(BlockPos devicePos, Class<? extends TileEntityNetworkDevice> targetDeviceClass)
    {
        this();
        this.devicePos = devicePos;
        this.targetDeviceClass = targetDeviceClass;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putLong("devicePos", devicePos.asLong());
        if(targetDeviceClass != null)
        {
            nbt.putString("targetClass", targetDeviceClass.getName());
        }
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        BlockPos devicePos = BlockPos.of(nbt.getLong("devicePos"));
        Class targetDeviceClass = null;
        try
        {
            Class targetClass = Class.forName(nbt.getString("targetClass"));
            if(TileEntityNetworkDevice.class.isAssignableFrom(targetClass))
            {
                targetDeviceClass = targetClass;
            }
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        BlockEntity tileEntity = Level.getBlockEntity(devicePos);
        if(tileEntity instanceof TileEntityNetworkDevice tileEntityNetworkDevice)
        {
            if(tileEntityNetworkDevice.isConnected())
            {
                Router router = tileEntityNetworkDevice.getRouter();
                if(router != null)
                {
                    if(targetDeviceClass != null)
                    {
                        foundDevices = router.getConnectedDevices(Level, targetDeviceClass);
                    }
                    else
                    {
                        foundDevices = router.getConnectedDevices(Level);
                    }
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        if(this.isSucessful())
        {
            ListTag deviceList = new ListTag();
            foundDevices.forEach(device -> deviceList.add(device.toTag(true)));
            nbt.put("network_devices", deviceList);
        }
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
