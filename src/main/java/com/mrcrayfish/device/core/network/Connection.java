package com.mrcrayfish.device.core.network;

import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public class Connection
{
    private UUID routerId;
    private BlockPos routerPos;

    private Connection() {}

    public Connection(Router router)
    {
        this.routerId = router.getId();
        this.routerPos = router.getPos();
    }

    public UUID getRouterId()
    {
        return routerId;
    }

    @Nullable
    public BlockPos getRouterPos()
    {
        return routerPos;
    }

    public void setRouterPos(BlockPos routerPos)
    {
        this.routerPos = routerPos;
    }

    @Nullable
    public Router getRouter(Level Level)
    {
        if(routerPos == null)
            return null;

        BlockEntity tileEntity = Level.getBlockEntity(routerPos);
        if(tileEntity instanceof TileEntityRouter router)
        {
            if(router.getRouter().getId().equals(routerId))
            {
                return router.getRouter();
            }
        }
        return null;
    }

    public boolean isConnected()
    {
        return routerPos != null;
    }

    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", routerId.toString());
        return tag;
    }

    public static Connection fromTag(TileEntityNetworkDevice device, CompoundTag tag)
    {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id"));
        return connection;
    }
}
