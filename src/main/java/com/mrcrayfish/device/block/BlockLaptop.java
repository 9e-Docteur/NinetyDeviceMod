package com.mrcrayfish.device.block;

import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.item.ItemFlashDrive;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class BlockLaptop extends BlockDevice.Colored
{
	public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
	public static final BooleanProperty OPEN = BooleanProperty.create("open");

	private static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
	private static final VoxelShape SHAPE_OPEN_EAST = Shapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));
	private static final VoxelShape SHAPE_OPEN_SOUTH = Shapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
	private static final VoxelShape SHAPE_OPEN_WEST = Shapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
	private static final VoxelShape SHAPE_CLOSED_NORTH = Block.box(1, 0, 1, 15, 2, 13);
	private static final VoxelShape SHAPE_CLOSED_EAST = Block.box(3, 0, 1, 15, 2, 15);
	private static final VoxelShape SHAPE_CLOSED_SOUTH = Block.box(1, 0, 3, 15, 2, 15);
	private static final VoxelShape SHAPE_CLOSED_WEST = Block.box(1, 0, 1, 13, 2, 15);

	public BlockLaptop(DyeColor color) {
		super(Properties.of(Material.HEAVY_METAL, color).strength(6f).sound(SoundType.METAL), color);
		registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE).setValue(OPEN, false));
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
		return pState.getValue(OPEN) ? switch (pState.getValue(FACING)) {
			case NORTH -> SHAPE_OPEN_NORTH;
			case EAST -> SHAPE_OPEN_EAST;
			case SOUTH -> SHAPE_OPEN_SOUTH;
			case WEST -> SHAPE_OPEN_WEST;
			default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
		} : switch (pState.getValue(FACING)) {
			case NORTH -> SHAPE_CLOSED_NORTH;
			case EAST -> SHAPE_CLOSED_EAST;
			case SOUTH -> SHAPE_CLOSED_SOUTH;
			case WEST -> SHAPE_CLOSED_WEST;
			default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
		};
	}

	@NotNull
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof TileEntityLaptop laptop) {
			if (player.isCrouching()) {
				if (!level.isClientSide) {
					laptop.openClose();
				}
				return InteractionResult.SUCCESS;
			} else {
				if (hit.getDirection() == state.getValue(FACING).getCounterClockWise(Direction.Axis.Y)) {
					ItemStack heldItem = player.getItemInHand(hand);
					if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemFlashDrive) {
						if (!level.isClientSide) {
							if (laptop.getFileSystem().setAttachedDrive(heldItem.copy())) {
								heldItem.shrink(1);
								return InteractionResult.CONSUME;
							} else {
								return InteractionResult.FAIL;
							}
						}
						return InteractionResult.PASS;
					}

					if (!level.isClientSide) {
						ItemStack stack = laptop.getFileSystem().removeAttachedDrive();
						if (stack != null) {
							BlockPos summonPos = pos.relative(state.getValue(FACING).getCounterClockWise(Direction.Axis.Y));
							level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
							TileEntityUtil.markBlockForUpdate(level, pos);
						}
					}
					return InteractionResult.SUCCESS;
				}
				if(level.isClientSide()) Minecraft.getInstance().setScreen(new Laptop(laptop));
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void removeTagsForDrop(CompoundTag tileEntityTag) {
		tileEntityTag.remove("open");
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TileEntityLaptop(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(TYPE, OPEN);
	}

	public enum Type implements StringRepresentable {
		BASE, SCREEN;

		@NotNull
		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
