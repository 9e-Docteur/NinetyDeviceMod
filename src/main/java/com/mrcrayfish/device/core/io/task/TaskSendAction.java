package com.mrcrayfish.device.core.io.task;

import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Author: MrCrayfish
 */
public class TaskSendAction extends Task
{
    private String uuid;
    private FileAction action;
    private BlockPos pos;

    private FileSystem.Response response;

    private TaskSendAction()
    {
        super("send_action");
    }

    public TaskSendAction(Drive drive, FileAction action)
    {
        this();
        this.uuid = drive.getUUID().toString();
        this.action = action;
        this.pos = Laptop.getPos();
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putString("uuid", uuid);
        nbt.put("action", action.toTag());
        nbt.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        FileAction action = FileAction.fromTag(nbt.getCompound("action"));
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            response = laptop.getFileSystem().readAction(nbt.getString("uuid"), action, Level);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        nbt.put("response", response.toTag());
    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
