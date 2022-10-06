package com.mrcrayfish.device.init;


import be.ninedocteur.ndm.NDMCreativeTabs;
import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.item.ItemFlashDrive;
import com.mrcrayfish.device.item.ItemMotherboard;
import com.mrcrayfish.device.item.ItemPaper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class DeviceItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "ndm");

    public static final RegistryObject<Item> FLASH_DRIVE = ITEMS.register("flash_drive", () -> new ItemFlashDrive(DyeColor.WHITE, new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<Item> ETHERNET_CABLE = ITEMS.register("ethernet_cable", () -> new ItemFlashDrive(DyeColor.WHITE, new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<Item> MOTHERBOARD = ITEMS.register("motherboard", () -> new ItemMotherboard(new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<Item> PAPER = ITEMS.register("paper_item", () -> new ItemPaper(DeviceBlocks.PAPER.get(), new Item.Properties().tab(NDMCreativeTabs.NDM)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
