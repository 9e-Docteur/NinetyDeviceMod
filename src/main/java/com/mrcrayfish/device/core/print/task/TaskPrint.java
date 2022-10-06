package com.mrcrayfish.device.core.print.task;

import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class TaskPrint extends Task
{
    private BlockPos devicePos;
    private UUID printerId;
    private IPrint print;

    private TaskPrint()
    {
        super("print");
    }

    public TaskPrint(BlockPos devicePos, NetworkDevice printer, IPrint print)
    {
        this();
        this.devicePos = devicePos;
        this.printerId = printer.getId();
        this.print = print;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putLong("devicePos", devicePos.asLong());
        nbt.putUUID("printerId", printerId);
        nbt.put("print", IPrint.writeToTag(print));
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        BlockEntity tileEntity = Level.getBlockEntity(BlockPos.of(nbt.getLong("devicePos")));
        if(tileEntity instanceof TileEntityNetworkDevice device)
        {
            Router router = device.getRouter();
            if(router != null)
            {
                TileEntityNetworkDevice printer = router.getDevice(Level, nbt.getUUID("printerId"));
                if(printer != null && printer instanceof TileEntityPrinter)
                {
                    IPrint print = IPrint.loadFromTag(nbt.getCompound("print"));
                    ((TileEntityPrinter) printer).addToQueue(print);
                    this.setSuccessful();
                }
            }
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
