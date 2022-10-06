package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class TaskUpdateApplicationData extends Task
{
    private int x, y, z;
    private String appId;
    private CompoundTag data;

    public TaskUpdateApplicationData()
    {
        super("update_application_data");
    }

    public TaskUpdateApplicationData(int x, int y, int z, @Nonnull String appId, @Nonnull CompoundTag data)
    {
        this();
        this.x = x;
        this.y = y;
        this.z = z;
        this.appId = appId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundTag tag)
    {
        tag.putInt("posX", this.x);
        tag.putInt("posY", this.y);
        tag.putInt("posZ", this.z);
        tag.putString("appId", this.appId);
        tag.put("appData", this.data);
    }

    @Override
    public void processRequest(CompoundTag tag, Level Level, Player player)
    {
        BlockEntity tileEntity = Level.getBlockEntity(new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            laptop.setApplicationData(tag.getString("appId"), tag.getCompound("appData"));
        }
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag)
    {

    }

    @Override
    public void processResponse(CompoundTag tag)
    {

    }
}
