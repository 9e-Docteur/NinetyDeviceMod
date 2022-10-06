package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.core.Device;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class NetworkDevice extends Device
{
    private NetworkDevice() {}

    public NetworkDevice(TileEntityNetworkDevice device)
    {
        super(device);
    }

    public NetworkDevice(UUID id, String name, Router router)
    {
        super(id, name);
    }

    public boolean isConnected(Level Level)
    {
        if(pos == null)
            return false;

        BlockEntity tileEntity = Level.getBlockEntity(pos);
        if(tileEntity instanceof TileEntityNetworkDevice device)
        {
            Router router = device.getRouter();
            return router != null && router.getId().equals(router.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntityNetworkDevice getDevice(Level Level)
    {
        if(pos == null)
            return null;

        BlockEntity tileEntity = Level.getBlockEntity(pos);
        if(tileEntity instanceof TileEntityNetworkDevice tileEntityNetworkDevice)
        {
            if(tileEntityNetworkDevice.getId().equals(getId()))
            {
                return tileEntityNetworkDevice;
            }
        }
        return null;
    }

    public CompoundTag toTag(boolean includePos)
    {
        CompoundTag tag = super.toTag(includePos);
        if(includePos && pos != null)
        {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static NetworkDevice fromTag(CompoundTag tag)
    {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        if(tag.contains("pos", Tag.TAG_LONG))
        {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
