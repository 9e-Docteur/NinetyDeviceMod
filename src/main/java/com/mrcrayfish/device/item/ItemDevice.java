package com.mrcrayfish.device.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ItemDevice extends BlockItem
{


    public ItemDevice(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    public @org.jetbrains.annotations.Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        if(stack.getTag() != null && stack.getTag().contains("display", Tag.TAG_COMPOUND))
        {
            tag.put("display", stack.getTag().get("display"));
        }
        return super.getShareTag(stack);
    }
}
