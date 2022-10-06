package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatFormatting;
import net.minecraft.Level.Level;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class ItemFlashDrive extends Item implements SubItems
{
    private final DyeColor color;

    public ItemFlashDrive(DyeColor color, Properties p_41383_) {
        super(p_41383_);
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @org.jetbrains.annotations.Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, tooltip, p_41424_);
        String colorName = color.getName().replace("_", " ");
        colorName = WordUtils.capitalize(colorName);
        tooltip.add(Component.literal("Color: " + ChatFormatting.BOLD.toString() + getFromColor(color).toString() + colorName));
    }

    @Override
    public NonNullList<ResourceLocation> getModels() {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for (DyeColor color : DyeColor.values())
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, Objects.requireNonNull(Registries.getId(this, Registry.ITEM_REGISTRY)).getPath().substring(5) + "/" + color.getName()));
        return modelLocations;
    }

    private static ChatFormatting getFromColor(DyeColor color)
    {
        switch(color)
        {
            case ORANGE: return ChatFormatting.GOLD;
            case MAGENTA: return ChatFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE: return ChatFormatting.BLUE;
            case YELLOW: return ChatFormatting.YELLOW;
            case LIME: return ChatFormatting.GREEN;
            case PINK: return ChatFormatting.LIGHT_PURPLE;
            case GRAY: return ChatFormatting.DARK_GRAY;
            case LIGHT_GRAY: return ChatFormatting.GRAY;
            case CYAN: return ChatFormatting.DARK_AQUA;
            case PURPLE: return ChatFormatting.DARK_PURPLE;
            case BLUE: return ChatFormatting.DARK_BLUE;
            case BROWN: return ChatFormatting.GOLD;
            case GREEN: return ChatFormatting.DARK_GREEN;
            case RED: return ChatFormatting.DARK_RED;
            case BLACK: return ChatFormatting.BLACK;
            default: return ChatFormatting.WHITE;
        }
    }
}
