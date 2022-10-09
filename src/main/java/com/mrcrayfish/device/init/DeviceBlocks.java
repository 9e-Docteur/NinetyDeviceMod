package com.mrcrayfish.device.init;


import be.ninedocteur.ndm.NDMCreativeTabs;
import be.ninedocteur.ndm.utils.NDMUtils;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.block.*;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DeviceBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> LAPTOP = registerBlock("laptop", () -> new BlockLaptop(DyeColor.WHITE));
    public static final RegistryObject<Block> PRINTER = registerBlock("printer", () -> new BlockPrinter(DyeColor.WHITE));
    public static final RegistryObject<Block> ROUTER = registerBlock("router", () -> new BlockRouter(DyeColor.WHITE));
    public static final RegistryObject<Block> PAPER = registerBlock("paper", () -> new BlockPaper());
    public static final RegistryObject<Block> CHAIR = registerBlock("chair", () -> new BlockOfficeChair(DyeColor.WHITE));

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