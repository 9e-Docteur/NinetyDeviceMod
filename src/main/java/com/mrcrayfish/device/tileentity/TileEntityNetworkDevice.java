package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.core.network.Connection;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.util.IColored;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityNetworkDevice extends TileEntityDevice implements Tickable
{
    private final Level level = Minecraft.getInstance().level;
    private int counter;
    private Connection connection;

    public TileEntityNetworkDevice(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }


    @Override
    public void tick(){
        if(level.isClientSide)
            return;

        if(connection != null)
        {
            if(++counter >= DeviceConfig.BEACON_INTERVAL.get() * 2)
            {
                connection.setRouterPos(null);
                counter = 0;
            }
        }
    }



    public void connect(Router router)
    {
        if(router == null)
        {
            if(connection != null)
            {
                Router connectedRouter = connection.getRouter(level);
                if(connectedRouter != null)
                {
                    connectedRouter.removeDevice(this);
                }
            }
            connection = null;
            return;
        }
        connection = new Connection(router);
        counter = 0;
        this.setChanged();
    }

    public Connection getConnection()
    {
        return connection;
    }

    @Nullable
    public Router getRouter()
    {
        return connection != null ? connection.getRouter(level) : null;
    }

    public boolean isConnected()
    {
        return connection != null && connection.isConnected();
    }

    public boolean receiveBeacon(Router router)
    {
        if(counter >= DeviceConfig.BEACON_INTERVAL.get() * 2)
        {
            connect(router);
            return true;
        }
        if(connection != null && connection.getRouterId().equals(router.getId()))
        {
            connection.setRouterPos(router.getPos());
            counter = 0;
            return true;
        }
        return false;
    }

    public int getSignalStrength()
    {
        BlockPos routerPos = connection.getRouterPos();
        if(routerPos != null)
        {
            double distance = Math.sqrt(worldPosition.distToCenterSqr(routerPos.getX() + 0.5, routerPos.getY() + 0.5, routerPos.getZ() + 0.5));
            double level = DeviceConfig.SIGNAL_RANGE.get() / 3.0;
            return distance > level * 2 ? 2 : distance > level ? 1 : 0;
        }
        return -1;
    }

    @Override
    public CompoundTag serializeNBT() {
        super.serializeNBT();
        CompoundTag compound = new CompoundTag();
        if(connection != null)
        {
            compound.put("connection", connection.toTag());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        if(compound.contains("connection", Tag.TAG_COMPOUND))
        {
            connection = Connection.fromTag(this, compound.getCompound("connection"));
        }
    }

    public static abstract class Colored extends TileEntityNetworkDevice implements IColored
    {
        private DyeColor color = DyeColor.RED;

        public Colored(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
            super(p_155228_, p_155229_, p_155230_);
        }

        @Override
        public CompoundTag serializeNBT() {
            super.serializeNBT();
            CompoundTag compound = new CompoundTag();
            compound.putByte("color", (byte) color.getId());
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            super.deserializeNBT(compound);
            if(compound.contains("color", Tag.TAG_BYTE))
            {
                this.color = DyeColor.byId(compound.getByte("color"));
            }
        }
        @Override
        public CompoundTag writeSyncTag()
        {
            CompoundTag tag = super.writeSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }
    }
}
