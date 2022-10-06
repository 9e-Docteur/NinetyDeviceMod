package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.init.DeviceTileEntites;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class TileEntityRouter extends TileEntityDevice.Colored
{
    private Router router;

    @OnlyIn(Dist.CLIENT)
    private int debugTimer;

    public TileEntityRouter(BlockPos p_155229_, BlockState p_155230_) {
        super(DeviceTileEntites.ROUTER.get(), p_155229_, p_155230_);
    }

    public Router getRouter()
    {
        if(router == null)
        {
            router = new Router(worldPosition);
            setChanged();
        }
        return router;
    }

    public void update()
    {
        if(!level.isClientSide)
        {
            getRouter().update(level);
        }
        else if(debugTimer > 0)
        {
            debugTimer--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isDebug()
    {
        return debugTimer > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void setDebug()
    {
        if(debugTimer <= 0)
        {
            debugTimer = 1200;
        }
        else
        {
            debugTimer = 0;
        }
    }

    @Override
    public String getDeviceName()
    {
        return "Router";
    }
    @Override
    public CompoundTag serializeNBT() {
        super.serializeNBT();
        CompoundTag compound = new CompoundTag();
        compound.put("router", getRouter().toTag(false));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if(nbt.contains("router", Tag.TAG_COMPOUND))
        {
            router = Router.fromTag(worldPosition, nbt.getCompound("router"));
        }
    }

    public void syncDevicesToClient()
    {
        pipeline.put("router", getRouter().toTag(true));
        sync();
    }
}
