package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class TaskGetStructure extends Task
{
    private String uuid;
    private BlockPos pos;

    private ServerFolder folder;

    private TaskGetStructure()
    {
        super("get_folder_structure");
    }

    public TaskGetStructure(Drive drive, BlockPos pos)
    {
        this();
        this.uuid = drive.getUUID().toString();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putString("uuid", uuid);
        nbt.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(nbt.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(Level, true).get(uuid);
            if(serverDrive != null)
            {
                folder = serverDrive.getDriveStructure();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        if(folder != null)
        {
            nbt.putString("file_name", folder.getName());
            nbt.put("structure", folder.toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
