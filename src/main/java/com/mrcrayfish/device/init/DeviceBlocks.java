package com.mrcrayfish.device.init;


import be.ninedocteur.ndm.NDMCreativeTabs;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeviceBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

//    public static final RegistryObject<Block> LAPTOP = registerBlock("white_laptop", () -> new BlockLaptop(DyeColor.WHITE));
//
//    public static final RegistryObject<Block> PRINTER = registerBlock("printer", () -> new BlockPrinter(DyeColor.WHITE));
//    public static final RegistryObject<Block> ROUTER = registerBlock("router", () -> new BlockRouter(DyeColor.WHITE));
//    public static final RegistryObject<Block> PAPER = registerBlock("paper", () -> new BlockPaper());
//    public static final RegistryObject<Block> CHAIR = registerBlock("chair", () -> new BlockOfficeChair(DyeColor.WHITE));
    
    //*---------

    public static final RegistryObject<BlockLaptop> WHITE_LAPTOP = registerBlock("white_laptop", () -> new BlockLaptop(DyeColor.WHITE));
    public static final RegistryObject<BlockLaptop> ORANGE_LAPTOP = registerBlock("orange_laptop", () -> new BlockLaptop(DyeColor.ORANGE));
    public static final RegistryObject<BlockLaptop> MAGENTA_LAPTOP = registerBlock("magenta_laptop", () -> new BlockLaptop(DyeColor.MAGENTA));
    public static final RegistryObject<BlockLaptop> LIGHT_BLUE_LAPTOP = registerBlock("light_blue_laptop", () -> new BlockLaptop(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<BlockLaptop> YELLOW_LAPTOP = registerBlock("yellow_laptop", () -> new BlockLaptop(DyeColor.YELLOW));
    public static final RegistryObject<BlockLaptop> LIME_LAPTOP = registerBlock("lime_laptop", () -> new BlockLaptop(DyeColor.LIME));
    public static final RegistryObject<BlockLaptop> PINK_LAPTOP = registerBlock("pink_laptop", () -> new BlockLaptop(DyeColor.PINK));
    public static final RegistryObject<BlockLaptop> GRAY_LAPTOP = registerBlock("gray_laptop", () -> new BlockLaptop(DyeColor.GRAY));
    public static final RegistryObject<BlockLaptop> LIGHT_GRAY_LAPTOP = registerBlock("light_gray_laptop", () -> new BlockLaptop(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<BlockLaptop> CYAN_LAPTOP = registerBlock("cyan_laptop", () -> new BlockLaptop(DyeColor.CYAN));
    public static final RegistryObject<BlockLaptop> PURPLE_LAPTOP = registerBlock("purple_laptop", () -> new BlockLaptop(DyeColor.PURPLE));
    public static final RegistryObject<BlockLaptop> BLUE_LAPTOP = registerBlock("blue_laptop", () -> new BlockLaptop(DyeColor.BLUE));
    public static final RegistryObject<BlockLaptop> BROWN_LAPTOP = registerBlock("brown_laptop", () -> new BlockLaptop(DyeColor.BROWN));
    public static final RegistryObject<BlockLaptop> GREEN_LAPTOP = registerBlock("green_laptop", () -> new BlockLaptop(DyeColor.GREEN));
    public static final RegistryObject<BlockLaptop> RED_LAPTOP = registerBlock("red_laptop", () -> new BlockLaptop(DyeColor.RED));
    public static final RegistryObject<BlockLaptop> BLACK_LAPTOP = registerBlock("black_laptop", () -> new BlockLaptop(DyeColor.BLACK));

    public static final RegistryObject<BlockPrinter> WHITE_PRINTER = registerBlock("white_printer", () -> new BlockPrinter(DyeColor.WHITE));
    public static final RegistryObject<BlockPrinter> ORANGE_PRINTER = registerBlock("orange_printer", () -> new BlockPrinter(DyeColor.ORANGE));
    public static final RegistryObject<BlockPrinter> MAGENTA_PRINTER = registerBlock("magenta_printer", () -> new BlockPrinter(DyeColor.MAGENTA));
    public static final RegistryObject<BlockPrinter> LIGHT_BLUE_PRINTER = registerBlock("light_blue_printer", () -> new BlockPrinter(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<BlockPrinter> YELLOW_PRINTER = registerBlock("yellow_printer", () -> new BlockPrinter(DyeColor.YELLOW));
    public static final RegistryObject<BlockPrinter> LIME_PRINTER = registerBlock("lime_printer", () -> new BlockPrinter(DyeColor.LIME));
    public static final RegistryObject<BlockPrinter> PINK_PRINTER = registerBlock("pink_printer", () -> new BlockPrinter(DyeColor.PINK));
    public static final RegistryObject<BlockPrinter> GRAY_PRINTER = registerBlock("gray_printer", () -> new BlockPrinter(DyeColor.GRAY));
    public static final RegistryObject<BlockPrinter> LIGHT_GRAY_PRINTER = registerBlock("light_gray_printer", () -> new BlockPrinter(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<BlockPrinter> CYAN_PRINTER = registerBlock("cyan_printer", () -> new BlockPrinter(DyeColor.CYAN));
    public static final RegistryObject<BlockPrinter> PURPLE_PRINTER = registerBlock("purple_printer", () -> new BlockPrinter(DyeColor.PURPLE));
    public static final RegistryObject<BlockPrinter> BLUE_PRINTER = registerBlock("blue_printer", () -> new BlockPrinter(DyeColor.BLUE));
    public static final RegistryObject<BlockPrinter> BROWN_PRINTER = registerBlock("brown_printer", () -> new BlockPrinter(DyeColor.BROWN));
    public static final RegistryObject<BlockPrinter> GREEN_PRINTER = registerBlock("green_printer", () -> new BlockPrinter(DyeColor.GREEN));
    public static final RegistryObject<BlockPrinter> RED_PRINTER = registerBlock("red_printer", () -> new BlockPrinter(DyeColor.RED));
    public static final RegistryObject<BlockPrinter> BLACK_PRINTER = registerBlock("black_printer", () -> new BlockPrinter(DyeColor.BLACK));

    public static final RegistryObject<BlockRouter> WHITE_ROUTER = registerBlock("white_router", () -> new BlockRouter(DyeColor.WHITE));
    public static final RegistryObject<BlockRouter> ORANGE_ROUTER = registerBlock("orange_router", () -> new BlockRouter(DyeColor.ORANGE));
    public static final RegistryObject<BlockRouter> MAGENTA_ROUTER = registerBlock("magenta_router", () -> new BlockRouter(DyeColor.MAGENTA));
    public static final RegistryObject<BlockRouter> LIGHT_BLUE_ROUTER = registerBlock("light_blue_router", () -> new BlockRouter(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<BlockRouter> YELLOW_ROUTER = registerBlock("yellow_router", () -> new BlockRouter(DyeColor.YELLOW));
    public static final RegistryObject<BlockRouter> LIME_ROUTER = registerBlock("lime_router", () -> new BlockRouter(DyeColor.LIME));
    public static final RegistryObject<BlockRouter> PINK_ROUTER = registerBlock("pink_router", () -> new BlockRouter(DyeColor.PINK));
    public static final RegistryObject<BlockRouter> GRAY_ROUTER = registerBlock("gray_router", () -> new BlockRouter(DyeColor.GRAY));
    public static final RegistryObject<BlockRouter> LIGHT_GRAY_ROUTER = registerBlock("light_gray_router", () -> new BlockRouter(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<BlockRouter> CYAN_ROUTER = registerBlock("cyan_router", () -> new BlockRouter(DyeColor.CYAN));
    public static final RegistryObject<BlockRouter> PURPLE_ROUTER = registerBlock("purple_router", () -> new BlockRouter(DyeColor.PURPLE));
    public static final RegistryObject<BlockRouter> BLUE_ROUTER = registerBlock("blue_router", () -> new BlockRouter(DyeColor.BLUE));
    public static final RegistryObject<BlockRouter> BROWN_ROUTER = registerBlock("brown_router", () -> new BlockRouter(DyeColor.BROWN));
    public static final RegistryObject<BlockRouter> GREEN_ROUTER = registerBlock("green_router", () -> new BlockRouter(DyeColor.GREEN));
    public static final RegistryObject<BlockRouter> RED_ROUTER = registerBlock("red_router", () -> new BlockRouter(DyeColor.RED));
    public static final RegistryObject<BlockRouter> BLACK_ROUTER = registerBlock("black_router", () -> new BlockRouter(DyeColor.BLACK));
    public static final RegistryObject<BlockPaper> PAPER = registerBlock("paper", BlockPaper::new);

    public static final RegistryObject<BlockOfficeChair> WHITE_OFFICE_CHAIR = registerBlock("white_office_chair", () -> new BlockOfficeChair(DyeColor.WHITE));
    public static final RegistryObject<BlockOfficeChair> ORANGE_OFFICE_CHAIR = registerBlock("orange_office_chair", () -> new BlockOfficeChair(DyeColor.ORANGE));
    public static final RegistryObject<BlockOfficeChair> MAGENTA_OFFICE_CHAIR = registerBlock("magenta_office_chair", () -> new BlockOfficeChair(DyeColor.MAGENTA));
    public static final RegistryObject<BlockOfficeChair> LIGHT_BLUE_OFFICE_CHAIR = registerBlock("light_blue_office_chair", () -> new BlockOfficeChair(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<BlockOfficeChair> YELLOW_OFFICE_CHAIR = registerBlock("yellow_office_chair", () -> new BlockOfficeChair(DyeColor.YELLOW));
    public static final RegistryObject<BlockOfficeChair> LIME_OFFICE_CHAIR = registerBlock("lime_office_chair", () -> new BlockOfficeChair(DyeColor.LIME));
    public static final RegistryObject<BlockOfficeChair> PINK_OFFICE_CHAIR = registerBlock("pink_office_chair", () -> new BlockOfficeChair(DyeColor.PINK));
    public static final RegistryObject<BlockOfficeChair> GRAY_OFFICE_CHAIR = registerBlock("gray_office_chair", () -> new BlockOfficeChair(DyeColor.GRAY));
    public static final RegistryObject<BlockOfficeChair> LIGHT_GRAY_OFFICE_CHAIR = registerBlock("light_gray_office_chair", () -> new BlockOfficeChair(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<BlockOfficeChair> CYAN_OFFICE_CHAIR = registerBlock("cyan_office_chair", () -> new BlockOfficeChair(DyeColor.CYAN));
    public static final RegistryObject<BlockOfficeChair> PURPLE_OFFICE_CHAIR = registerBlock("purple_office_chair", () -> new BlockOfficeChair(DyeColor.PURPLE));
    public static final RegistryObject<BlockOfficeChair> BLUE_OFFICE_CHAIR = registerBlock("blue_office_chair", () -> new BlockOfficeChair(DyeColor.BLUE));
    public static final RegistryObject<BlockOfficeChair> BROWN_OFFICE_CHAIR = registerBlock("brown_office_chair", () -> new BlockOfficeChair(DyeColor.BROWN));
    public static final RegistryObject<BlockOfficeChair> GREEN_OFFICE_CHAIR = registerBlock("green_office_chair", () -> new BlockOfficeChair(DyeColor.GREEN));
    public static final RegistryObject<BlockOfficeChair> RED_OFFICE_CHAIR = registerBlock("red_office_chair", () -> new BlockOfficeChair(DyeColor.RED));
    public static final RegistryObject<BlockOfficeChair> BLACK_OFFICE_CHAIR = registerBlock("black_office_chair", () -> new BlockOfficeChair(DyeColor.BLACK));

    public static Stream<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(entry -> entry.get());
    }

    public static List<BlockLaptop> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof BlockLaptop).map(block -> (BlockLaptop) block).toList();
    }

    public static List<BlockPrinter> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof BlockPrinter).map(block -> (BlockPrinter) block).toList();
    }

    public static List<BlockRouter> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof BlockRouter).map(block -> (BlockRouter) block).toList();
    }

    public static List<BlockOfficeChair> getAllOfficeChairs() {
        return getAllBlocks().filter(block -> block instanceof BlockOfficeChair).map(block -> (BlockOfficeChair) block).toList();
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    private static <T extends Block> RegistryObject<T> registerBlockOnly(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        DeviceItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(NDMCreativeTabs.NDM)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}