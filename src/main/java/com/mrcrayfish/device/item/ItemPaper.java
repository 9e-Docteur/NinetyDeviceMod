package com.mrcrayfish.device.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ItemPaper extends BlockItem
{


    public ItemPaper(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    public @org.jetbrains.annotations.Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag != null)
        {
            CompoundTag copy = tag.copy();
            copy.remove("BlockEntityTag");
            return copy;
        }
        return null;
    }
}
