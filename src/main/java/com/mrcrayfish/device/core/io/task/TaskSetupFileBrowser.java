package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class TaskSetupFileBrowser extends Task
{
    private BlockPos pos;
    private boolean includeMain;

    private AbstractDrive mainDrive;
    private Map<UUID, AbstractDrive> availableDrives;

    private TaskSetupFileBrowser()
    {
        super("get_file_system");
    }

    public TaskSetupFileBrowser(BlockPos pos, boolean includeMain)
    {
        this();
        this.pos = pos;
        this.includeMain = includeMain;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putLong("pos", pos.asLong());
        nbt.putBoolean("include_main", includeMain);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            FileSystem fileSystem = laptop.getFileSystem();
            if(nbt.getBoolean("include_main"))
            {
                mainDrive = fileSystem.getMainDrive();
            }
            availableDrives = fileSystem.getAvailableDrives(Level, false);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        if(this.isSucessful())
        {
            if(mainDrive != null)
            {
                CompoundTag mainDriveTag = new CompoundTag();
                mainDriveTag.putString("name", mainDrive.getName());
                mainDriveTag.putString("uuid", mainDrive.getUUID().toString());
                mainDriveTag.putString("type", mainDrive.getType().toString());
                nbt.put("main_drive", mainDriveTag);
                nbt.put("structure", mainDrive.getDriveStructure().toTag());
            }

            ListTag driveList = new ListTag();
            availableDrives.forEach((k, v) -> {
                CompoundTag driveTag = new CompoundTag();
                driveTag.putString("name", v.getName());
                driveTag.putString("uuid", v.getUUID().toString());
                driveTag.putString("type", v.getType().toString());
                driveList.add(driveTag);
            });
            nbt.put("available_drives", driveList);
        }
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
