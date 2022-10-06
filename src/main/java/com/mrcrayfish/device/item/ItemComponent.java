package com.mrcrayfish.device.item;

import be.ninedocteur.ndm.NDMCreativeTabs;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemComponent extends Item
{
    public ItemComponent(Properties properties)
    {
        super(properties.tab(NDMCreativeTabs.NDM));
    }
}
