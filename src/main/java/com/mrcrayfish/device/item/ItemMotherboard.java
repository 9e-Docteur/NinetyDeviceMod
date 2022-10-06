package com.mrcrayfish.device.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemMotherboard extends ItemComponent
{


    public ItemMotherboard(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level p_41422_, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag p_41424_) {
        super.appendHoverText(stack, p_41422_, tooltip, p_41424_);
        CompoundTag tag = stack.getTag();
        if(!Screen.hasShiftDown())
        {
            tooltip.add(net.minecraft.network.chat.Component.literal("CPU: " + getComponentStatus(tag, "cpu")));
            tooltip.add(net.minecraft.network.chat.Component.literal("RAM: " + getComponentStatus(tag, "ram")));
            tooltip.add(net.minecraft.network.chat.Component.literal("GPU: " + getComponentStatus(tag, "gpu")));
            tooltip.add(net.minecraft.network.chat.Component.literal("WIFI: " + getComponentStatus(tag, "wifi")));
            tooltip.add(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + "Hold shift for help"));
        }
        else
        {
            tooltip.add(net.minecraft.network.chat.Component.literal("To add the required components"));
            tooltip.add(net.minecraft.network.chat.Component.literal("place the motherboard and the"));
            tooltip.add(net.minecraft.network.chat.Component.literal("corresponding component into a"));
            tooltip.add(net.minecraft.network.chat.Component.literal("crafting table to combine them."));
        }
    }

    private String getComponentStatus(CompoundTag tag, String component)
    {
        if(tag != null && tag.contains("components", Tag.TAG_COMPOUND))
        {
            CompoundTag components = tag.getCompound("components");
            if(components.contains(component, Tag.TAG_BYTE))
            {
                return ChatFormatting.GREEN + "Added";
            }
        }
        return ChatFormatting.RED + "Missing";
    }

    public static class Component extends ItemComponent
    {
        public Component(Properties properties)
        {
            super(properties);
        }
    }
}
