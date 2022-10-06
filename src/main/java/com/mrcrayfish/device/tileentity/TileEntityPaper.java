package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.init.DeviceTileEntites;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class TileEntityPaper extends TileEntitySync
{
    private IPrint print;
    private byte rotation;

    public TileEntityPaper(BlockPos p_155229_, BlockState p_155230_) {
        super(DeviceTileEntites.PAPER.get(), p_155229_, p_155230_);
    }

    public void nextRotation()
    {
        rotation++;
        if(rotation > 7)
        {
            rotation = 0;
        }
        pipeline.putByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
    }

    public float getRotation()
    {
        return rotation * 45F;
    }

    @Nullable
    public IPrint getPrint()
    {
        return print;
    }

    @Override
    public CompoundTag serializeNBT() {
        super.serializeNBT();
        CompoundTag compound = new CompoundTag();
        if(print != null)
        {
            compound.put("print", IPrint.writeToTag(print));
        }
        compound.putByte("rotation", rotation);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        if(compound.contains("print", Tag.TAG_COMPOUND))
        {
            print = IPrint.loadFromTag(compound.getCompound("print"));
        }
        if(compound.contains("rotation", Tag.TAG_BYTE))
        {
            rotation = compound.getByte("rotation");
        }
    }

    @Override
    public CompoundTag writeSyncTag()
    {
        CompoundTag tag = new CompoundTag();
        if(print != null)
        {
            tag.put("print", IPrint.writeToTag(print));
        }
        tag.putByte("rotation", rotation);
        return tag;
    }

    private void playSound(SoundEvent sound)
    {
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
