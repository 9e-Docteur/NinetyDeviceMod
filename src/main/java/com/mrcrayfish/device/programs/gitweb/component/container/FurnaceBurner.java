package com.mrcrayfish.device.programs.gitweb.component.container;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceBurner {
    public static int getBurnTime(ItemStack stack) {
        var a = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return a == null ? 1600 : a;
    }
}
