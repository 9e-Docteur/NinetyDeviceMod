package com.mrcrayfish.device.core;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class Device
{
    protected UUID id;
    protected String name;
    protected BlockPos pos;

    protected Device() {}

    public Device(TileEntityDevice device)
    {
        this.id = device.getId();
        update(device);
    }

    public Device(UUID id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    @Nullable
    public BlockPos getPos()
    {
        return pos;
    }

    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    public void update(TileEntityDevice device)
    {
        name = device.getCustomName();
        pos = device.getBlockPos();
    }

    @Nullable
    public TileEntityDevice getDevice(Level Level)
    {
        if(pos == null)
            return null;

        BlockEntity tileEntity = Level.getBlockEntity(pos);
        if(tileEntity instanceof TileEntityDevice tileEntityDevice)
        {
            if(tileEntityDevice.getId().equals(getId()))
            {
                return tileEntityDevice;
            }
        }
        return null;
    }

    public CompoundTag toTag(boolean includePos)
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putString("name", name);
        if(includePos && pos != null)
        {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static Device fromTag(CompoundTag tag)
    {
        Device device = new Device();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        if(tag.contains("pos", Tag.TAG_LONG))
        {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
