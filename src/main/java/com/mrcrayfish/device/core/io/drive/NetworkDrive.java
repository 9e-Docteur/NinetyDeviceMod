package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.action.FileAction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public final class NetworkDrive extends AbstractDrive
{
    private final BlockPos pos;

    public NetworkDrive(String name, BlockPos pos)
    {
        super(name);
        this.pos = pos;
        this.root = null;
    }

    @Nullable
    @Override
    public ServerFolder getRoot(Level Level)
    {
        BlockEntity tileEntity = Level.getBlockEntity(pos);
        if(tileEntity instanceof Interface impl)
        {
            AbstractDrive drive = impl.getDrive();
            if(drive != null)
            {
                return drive.getRoot(Level);
            }
        }
        return null;
    }

    @Override
    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, Level Level)
    {
        BlockEntity tileEntity = Level.getBlockEntity(pos);
        if(tileEntity instanceof Interface impl)
        {
            AbstractDrive drive = impl.getDrive();
            if(drive.handleFileAction(fileSystem, action, Level).getStatus() == FileSystem.Status.SUCCESSFUL)
            {
                tileEntity.setChanged();
                return FileSystem.createSuccessResponse();
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "The network drive could not be found");
    }

    @Nullable
    @Override
    public ServerFolder getFolder(String path)
    {
        return null;
    }

    @Override
    public Type getType()
    {
        return Type.NETWORK;
    }

    @Override
    public CompoundTag toTag()
    {
        return null;
    }

    public interface Interface
    {
        AbstractDrive getDrive();

        boolean canAccessDrive();
    }
}
