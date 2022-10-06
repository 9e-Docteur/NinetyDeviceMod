package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Author: MrCrayfish
 */
public class TaskGetMainDrive extends Task
{
    private BlockPos pos;

    private AbstractDrive mainDrive;

    private TaskGetMainDrive()
    {
        super("get_main_drive");
    }

    public TaskGetMainDrive(BlockPos pos)
    {
        this();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            FileSystem fileSystem = laptop.getFileSystem();
            mainDrive = fileSystem.getMainDrive();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        if(this.isSucessful())
        {
            CompoundTag mainDriveTag = new CompoundTag();
            mainDriveTag.putString("name", mainDrive.getName());
            mainDriveTag.putString("uuid", mainDrive.getUUID().toString());
            mainDriveTag.putString("type", mainDrive.getType().toString());
            nbt.put("main_drive", mainDriveTag);
            nbt.put("structure", mainDrive.getDriveStructure().toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {
        if(this.isSucessful())
        {
            if(Minecraft.getInstance().screen instanceof Laptop)
            {
                CompoundTag structureTag = nbt.getCompound("structure");
                Drive drive = new Drive(nbt.getCompound("main_drive"));
                drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                drive.getRoot().validate();

                if(Laptop.getMainDrive() == null)
                {
                    Laptop.setMainDrive(drive);
                }
            }
        }
    }
}
