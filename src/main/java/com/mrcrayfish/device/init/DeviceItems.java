package com.mrcrayfish.device.init;


import be.ninedocteur.ndm.NDMCreativeTabs;
import com.mrcrayfish.device.block.BlockLaptop;
import com.mrcrayfish.device.item.ItemEthernetCable;
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

    public static final RegistryObject<Item> ETHERNET_CABLE = ITEMS.register("ethernet_cable", () -> new ItemEthernetCable(new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<Item> MOTHERBOARD = ITEMS.register("motherboard", () -> new ItemMotherboard(new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<Item> PAPER = ITEMS.register("paper_item", () -> new ItemPaper(DeviceBlocks.PAPER.get(), new Item.Properties().tab(NDMCreativeTabs.NDM)));
    public static final RegistryObject<ItemFlashDrive> WHITE_FLASH_DRIVE = ITEMS.register("white_flash_drive", () -> new ItemFlashDrive(DyeColor.WHITE));
    public static final RegistryObject<ItemFlashDrive> ORANGE_FLASH_DRIVE = ITEMS.register("orange_flash_drive", () -> new ItemFlashDrive(DyeColor.ORANGE));
    public static final RegistryObject<ItemFlashDrive> MAGENTA_FLASH_DRIVE = ITEMS.register("magenta_flash_drive", () -> new ItemFlashDrive(DyeColor.MAGENTA));
    public static final RegistryObject<ItemFlashDrive> LIGHT_BLUE_FLASH_DRIVE = ITEMS.register("light_blue_flash_drive", () -> new ItemFlashDrive(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<ItemFlashDrive> YELLOW_FLASH_DRIVE = ITEMS.register("yellow_flash_drive", () -> new ItemFlashDrive(DyeColor.YELLOW));
    public static final RegistryObject<ItemFlashDrive> LIME_FLASH_DRIVE = ITEMS.register("lime_flash_drive", () -> new ItemFlashDrive(DyeColor.LIME));
    public static final RegistryObject<ItemFlashDrive> PINK_FLASH_DRIVE = ITEMS.register("pink_flash_drive", () -> new ItemFlashDrive(DyeColor.PINK));
    public static final RegistryObject<ItemFlashDrive> GRAY_FLASH_DRIVE = ITEMS.register("gray_flash_drive", () -> new ItemFlashDrive(DyeColor.GRAY));
    public static final RegistryObject<ItemFlashDrive> LIGHT_GRAY_FLASH_DRIVE = ITEMS.register("light_gray_flash_drive", () -> new ItemFlashDrive(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<ItemFlashDrive> CYAN_FLASH_DRIVE = ITEMS.register("cyan_flash_drive", () -> new ItemFlashDrive(DyeColor.CYAN));
    public static final RegistryObject<ItemFlashDrive> PURPLE_FLASH_DRIVE = ITEMS.register("purple_flash_drive", () -> new ItemFlashDrive(DyeColor.PURPLE));
    public static final RegistryObject<ItemFlashDrive> BLUE_FLASH_DRIVE = ITEMS.register("blue_flash_drive", () -> new ItemFlashDrive(DyeColor.BLUE));
    public static final RegistryObject<ItemFlashDrive> BROWN_FLASH_DRIVE = ITEMS.register("brown_flash_drive", () -> new ItemFlashDrive(DyeColor.BROWN));
    public static final RegistryObject<ItemFlashDrive> GREEN_FLASH_DRIVE = ITEMS.register("green_flash_drive", () -> new ItemFlashDrive(DyeColor.GREEN));
    public static final RegistryObject<ItemFlashDrive> RED_FLASH_DRIVE = ITEMS.register("red_flash_drive", () -> new ItemFlashDrive(DyeColor.RED));
    public static final RegistryObject<ItemFlashDrive> BLACK_FLASH_DRIVE = ITEMS.register("black_flash_drive", () -> new ItemFlashDrive(DyeColor.BLACK));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
