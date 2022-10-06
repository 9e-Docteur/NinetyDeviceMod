package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.ServerFile;
import com.mrcrayfish.device.core.io.ServerFolder;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class TaskGetFiles extends Task
{
    private String uuid;
    private String path;
    private BlockPos pos;

    private List<ServerFile> files;

    private TaskGetFiles()
    {
        super("get_files");
    }

    public TaskGetFiles(Folder folder, BlockPos pos)
    {
        this();
        this.uuid = folder.getDrive().getUUID().toString();
        this.path = folder.getPath();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putString("uuid", uuid);
        nbt.putString("path", path);
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
                ServerFolder found = serverDrive.getFolder(nbt.getString("path"));
                if(found != null)
                {
                    this.files = found.getFiles().stream().filter(f -> !f.isFolder()).collect(Collectors.toList());
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        if(this.files != null)
        {
            ListTag list = new ListTag();
            this.files.forEach(f -> {
                CompoundTag fileTag = new CompoundTag();
                fileTag.putString("file_name", f.getName());
                fileTag.put("data", f.toTag());
                list.add(fileTag);
            });
            nbt.put("files", list);
        }
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }

    protected static String compileDirectory(ServerFile file)
    {
        if(file.getParent() == null || file.getParent().getParent() == null)
            return "/";

        StringBuilder builder = new StringBuilder();
        ServerFolder parent = file.getParent();
        while(parent != null)
        {
            builder.insert(0, "/" + parent.getName());
            if(parent.getParent() != null)
            {
                return builder.toString();
            }
            parent = parent.getParent();
        }
        return builder.toString();
    }
}
