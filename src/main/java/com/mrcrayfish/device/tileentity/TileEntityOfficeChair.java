package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.util.IColored;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TileEntityOfficeChair extends TileEntitySync implements IColored
{
    private DyeColor color = DyeColor.RED;

    public TileEntityOfficeChair(BlockPos p_155229_, BlockState p_155230_) {
        super(DeviceTileEntites.CHAIR.get(), p_155229_, p_155230_);
    }

    @Override
    public DyeColor getColor()
    {
        return color;
    }

    @Override
    public void setColor(DyeColor color)
    {
        this.color = color;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if(nbt.contains("color", Tag.TAG_BYTE))
        {
            color = DyeColor.byFireworkColor(nbt.getByte("color"));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByte("color", (byte) color.getId());
        return compoundTag;
    }

    @Override
    public CompoundTag writeSyncTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

//    @OnlyIn(Dist.CLIENT)
//    public float getRotation()
//    {
//        List<EntitySeat> seats = Level.getEntitiesWithinAABB(EntitySeat.class, new AxisAlignedBB(pos));
//        if(!seats.isEmpty())
//        {
//            EntitySeat seat = seats.get(0);
//            if(seat.getControllingPassenger() != null)
//            {
//                if(seat.getControllingPassenger() instanceof EntityLivingBase)
//                {
//                    EntityLivingBase living = (EntityLivingBase) seat.getControllingPassenger();
//                    living.renderYawOffset = living.rotationYaw;
//                    living.prevRenderYawOffset = living.rotationYaw;
//                    return living.rotationYaw;
//                }
//                return seat.getControllingPassenger().rotationYaw;
//            }
//        }
//        return getBlockMetadata() * 90F + 180F;
//    }
}
