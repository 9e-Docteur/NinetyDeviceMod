package com.mrcrayfish.device.core;

import com.mrcrayfish.device.programs.system.object.ColorScheme;
import net.minecraft.nbt.CompoundTag;

/**
 * Author: MrCrayfish
 */
public class Settings
{
    private static boolean showAllApps = true;

    private ColorScheme colorScheme = new ColorScheme();

    public static void setShowAllApps(boolean showAllApps)
    {
        Settings.showAllApps = showAllApps;
    }

    public static boolean isShowAllApps()
    {
        return Settings.showAllApps;
    }

    public ColorScheme getColorScheme()
    {
        return colorScheme;
    }

    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("showAllApps", showAllApps);
        tag.put("colorScheme", colorScheme.toTag());
        return tag;
    }

    public static Settings fromTag(CompoundTag tag)
    {
        //showAllApps = tag.getBoolean("showAllApps");

        Settings settings = new Settings();
        settings.colorScheme = ColorScheme.fromTag(tag.getCompound("colorScheme"));
        return settings;
    }
}
