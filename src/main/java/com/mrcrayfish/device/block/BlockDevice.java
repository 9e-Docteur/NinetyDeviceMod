package com.mrcrayfish.device.block;

import com.mrcrayfish.device.tileentity.TileEntityDevice;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Author: MrCrayfish
 */
public abstract class BlockDevice extends HorizontalDirectionalBlock implements EntityBlock
{


    protected BlockDevice(Properties p_54120_) {
        super(p_54120_.strength(0.5f));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, Objects.requireNonNull(pContext.getPlayer(), "Player in block placement context is null.").getDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityDevice deviceBlockEntity) {
            if (stack.hasCustomHoverName()) {
                deviceBlockEntity.setCustomName(stack.getHoverName().getString());
            }
        }
    }


    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TileEntityDevice device) {
                CompoundTag blockEntityTag = new CompoundTag();
                blockEntity.saveWithoutMetadata();
                blockEntityTag.remove("id");

                removeTagsForDrop(blockEntityTag);

                CompoundTag tag = new CompoundTag();
                tag.put("BlockEntityTag", blockEntityTag);

                level.removeBlock(pos, false);
                return;
            }
        }
        super.destroy(level, pos, state);
    }

    protected void removeTagsForDrop(CompoundTag blockEntityTag) {

    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return getTicker();
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, Level level, @NotNull BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    public static abstract class Colored extends BlockDevice {
        private final DyeColor color;

        protected Colored(Properties properties, DyeColor color) {
            super(properties);
            this.color = color;
        }

        public DyeColor getColor() {
            return color;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
            super.createBlockStateDefinition(pBuilder);
            pBuilder.add(FACING);
        }
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (pLevel, pPos, pState, pBlockEntity) -> {
            if (pBlockEntity instanceof Tickable) {
                ((Tickable) pBlockEntity).tick();
            }
        };
    }

}
