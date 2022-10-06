package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntitySync extends BlockEntity
{
    protected CompoundTag pipeline = new CompoundTag();

    public TileEntitySync(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public void sync()
    {
        setChanged(level, getBlockPos(), getBlockState());
        setChanged();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.deserializeCaps(pkt.getTag());
    }

    @Override
    public final CompoundTag getUpdateTag()
    {
        if(!pipeline.isEmpty())
        {
            CompoundTag updateTag = super.serializeNBT();
            pipeline = new CompoundTag();
            return updateTag;
        }
        return super.serializeNBT();
    }



    public abstract CompoundTag writeSyncTag();

//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket()
//    {
//        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
//    }

    public CompoundTag getPipeline()
    {
        return pipeline;
    }
}
