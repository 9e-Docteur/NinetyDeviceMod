package com.mrcrayfish.device.init;

import com.mrcrayfish.device.tileentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DeviceTileEntites
{
	public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
			DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "ndm");

	public static RegistryObject<BlockEntityType<TileEntityLaptop>> LAPTOP =
			TILE_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(
					TileEntityLaptop::new, DeviceBlocks.LAPTOP.get()).build(null));


	public static RegistryObject<BlockEntityType<TileEntityPrinter>> PRINTER =
			TILE_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(
					TileEntityPrinter::new, DeviceBlocks.PRINTER.get()).build(null));
	public static RegistryObject<BlockEntityType<TileEntityPaper>> PAPER =
			TILE_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(
					TileEntityPaper::new, DeviceBlocks.PAPER.get()).build(null));
	public static RegistryObject<BlockEntityType<TileEntityOfficeChair>> CHAIR =
			TILE_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(
					TileEntityOfficeChair::new, DeviceBlocks.CHAIR.get()).build(null));
	public static RegistryObject<BlockEntityType<TileEntityRouter>> ROUTER =
			TILE_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(
					TileEntityRouter::new, DeviceBlocks.ROUTER.get()).build(null));


	public static void register(IEventBus eventBus)
    {
		TILE_ENTITIES.register(eventBus);
	}
}
