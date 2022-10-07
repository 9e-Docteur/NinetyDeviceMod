package com.mrcrayfish.device.api.print;

import com.mrcrayfish.device.init.DeviceBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

//printing somethings takes makes ink cartridge take damage. cartridge can only stack to one

/**
 * Author: MrCrayfish
 */
public interface IPrint
{
    String getName();

    /**
     * Gets the speed of the print. The higher the value, the longer it will take to print.
     * @return the speed of this print
     */
    int speed();

    /**
     * Gets whether or not this print requires colored ink.
     * @return if print requires ink
     */
    boolean requiresColor();

    /**
     * Converts print into an NBT tag compound. Used for the renderer.
     * @return nbt form of print
     */
    CompoundTag toTag();

    void fromTag(CompoundTag tag);

    @OnlyIn(Dist.CLIENT)
    Class<? extends Renderer> getRenderer();

    static CompoundTag writeToTag(IPrint print)
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", PrintingManager.getPrintIdentifier(print));
        tag.put("data", print.toTag());
        return tag;
    }

    @Nullable
    static IPrint loadFromTag(CompoundTag tag)
    {
        IPrint print = PrintingManager.getPrint(tag.getString("type"));
        if(print != null)
        {
            print.fromTag(tag.getCompound("data"));
            return print;
        }
        return null;
    }

    static ItemStack generateItem(IPrint print)
    {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.put("print", writeToTag(print));

        CompoundTag itemTag = new CompoundTag();
        itemTag.put("BlockEntityTag", blockEntityTag);

        ItemStack stack = new ItemStack(DeviceBlocks.PAPER.get());
        stack.setTag(itemTag);

        if(print.getName() != null && !print.getName().isEmpty())
        {
            stack.setHoverName(Component.literal(print.getName()));
        }
        return stack;
    }

    interface Renderer
    {
        boolean render(CompoundTag data);
    }
}
