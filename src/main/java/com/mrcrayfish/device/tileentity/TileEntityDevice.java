package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.util.IColored;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Author: MrCrayfish
 */
public abstract class TileEntityDevice extends TileEntitySync
{
    private DyeColor color = DyeColor.RED;
    private UUID deviceId;
    private String name;

    public TileEntityDevice(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public final UUID getId()
    {
        if(deviceId == null)
        {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    public abstract String getDeviceName();

    public String getCustomName()
    {
        return hasCustomName() ? name : getDeviceName();
    }

    public void setCustomName(String name)
    {
       this.name = name;
    }

    public boolean hasCustomName()
    {
        return !StringUtils.isEmpty(name);
    }


    @Override
    public CompoundTag serializeNBT() {
        super.serializeNBT();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("deviceId", getId().toString());
        if(hasCustomName())
        {
            compoundTag.putString("name", name);
        }
        compoundTag.putByte("color", (byte) color.getId());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if(nbt.getString("deviceId", Tag.TAG_STRING))
        {
            deviceId = UUID.fromString(nbt.getString("deviceId"));
        }
        if(nbt.getString("name", Tag.TAG_STRING))
        {
            name = nbt.getString("name");
        }
        if(nbt.getString("color", Tag.TAG_BYTE))
        {
            this.color = DyeColor.byId(nbt.getByte("color"));
        }
    }

    @Override
    public CompoundTag writeSyncTag()
    {
        CompoundTag tag = new CompoundTag();
        if(hasCustomName())
        {
            tag.putString("name", name);
        }
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    public static abstract class Colored extends TileEntityDevice implements IColored
    {
        private DyeColor color = DyeColor.RED;

        public Colored(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
            super(p_155228_, p_155229_, p_155230_);
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            if(nbt.put("color", Tag.TAG_BYTE))
            {
                this.color = DyeColor.byId(nbt.getByte("color"));
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            super.serializeNBT();
            CompoundTag compound = new CompoundTag();
            compound.putByte("color", (byte) color.getId());
            return compound;
        }

        @Override
        public CompoundTag writeSyncTag()
        {
            CompoundTag tag = super.writeSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        @Override
        public final void setColor(DyeColor color)
        {
            this.color = color;
        }

        @Override
        public final DyeColor getColor()
        {
            return color;
        }
    }
}
