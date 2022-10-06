package com.mrcrayfish.device.core.task;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Author: MrCrayfish
 */
public class TaskInstallApp extends Task
{
    private String appId;
    private BlockPos laptopPos;
    private boolean install;

    private TaskInstallApp()
    {
        super("install_app");
    }

    public TaskInstallApp(AppInfo info, BlockPos laptopPos, boolean install)
    {
        this();
        this.appId = info.getFormattedId();
        this.laptopPos = laptopPos;
        this.install = install;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putString("appId", appId);
        nbt.putLong("pos", laptopPos.asLong());
        nbt.putBoolean("install", install);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        String appId = nbt.getString("appId");
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if(tileEntity instanceof TileEntityLaptop laptop)
        {
            CompoundTag systemData = laptop.getSystemData();
            ListTag tagList = systemData.getList("InstalledApps", Tag.TAG_STRING);

            if(nbt.getBoolean("install"))
            {
                for(int i = 0; i < tagList.size(); i++)
                {
                    if(tagList.getString(i).equals(appId))
                    {
                        return;
                    }
                }
                tagList.add(StringTag.valueOf(appId));
                this.setSuccessful();
            }
            else
            {
                for(int i = 0; i < tagList.size(); i++)
                {
                    if(tagList.getString(i).equals(appId))
                    {
                        tagList.remove(i);
                        this.setSuccessful();
                    }
                }
            }
            systemData.put("InstalledApps", tagList);
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {

    }

    @Override
    public void processResponse(CompoundTag nbt)
    {

    }
}
