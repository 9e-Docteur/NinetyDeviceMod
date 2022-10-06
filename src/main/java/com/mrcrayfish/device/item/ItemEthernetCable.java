package com.mrcrayfish.device.item;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.core.network.Router;
import com.mrcrayfish.device.tileentity.TileEntityNetworkDevice;
import com.mrcrayfish.device.tileentity.TileEntityRouter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ItemEthernetCable extends Item
{


    public ItemEthernetCable(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level Level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        if(!Level.isClientSide)
        {
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            BlockEntity tileEntity = Level.getBlockEntity(pos);

            if(tileEntity instanceof TileEntityRouter tileEntityRouter)
            {
                if(!heldItem.hasTag())
                {
                    sendGameInfoMessage(player, "message.invalid_cable");
                    return InteractionResult.SUCCESS;
                }

                Router router = tileEntityRouter.getRouter();

                CompoundTag tag = heldItem.getTag();
                BlockPos devicePos = BlockPos.of(tag.getLong("pos"));

                BlockEntity tileEntity1 = Level.getBlockEntity(devicePos);
                if(tileEntity1 instanceof TileEntityNetworkDevice tileEntityNetworkDevice)
                {
                    if(!router.isDeviceRegistered(tileEntityNetworkDevice))
                    {
                        if(router.addDevice(tileEntityNetworkDevice))
                        {
                            tileEntityNetworkDevice.connect(router);
                            heldItem.shrink(1);
                            if(getDistance(tileEntity1.getBlockPos(), tileEntityRouter.getBlockPos()) > DeviceConfig.getSignalRange())
                            {
                                sendGameInfoMessage(player, "message.successful_registered");
                            }
                            else
                            {
                                sendGameInfoMessage(player, "message.successful_connection");
                            }
                        }
                        else
                        {
                            sendGameInfoMessage(player, "message.router_max_devices");
                        }
                    }
                    else
                    {
                        sendGameInfoMessage(player, "message.device_already_connected");
                    }
                }
                else
                {
                    if(router.addDevice(tag.getUUID("id"), tag.getString("name")))
                    {
                        heldItem.shrink(1);
                        sendGameInfoMessage(player, "message.successful_registered");
                    }
                    else
                    {
                        sendGameInfoMessage(player, "message.router_max_devices");
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if(tileEntity instanceof TileEntityNetworkDevice tileEntityNetworkDevice)
            {
                heldItem.setTag(new CompoundTag());
                CompoundTag tag = heldItem.getTag();
                tag.putUUID("id", tileEntityNetworkDevice.getId());
                tag.putString("name", tileEntityNetworkDevice.getCustomName());
                tag.putLong("pos", tileEntityNetworkDevice.getBlockPos().asLong());

                sendGameInfoMessage(player, "message.select_router");
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void sendGameInfoMessage(Player player, String message)
    {
        if(player instanceof ServerPlayer)
        {
            player.sendSystemMessage(Component.translatable((message)));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if(!p_41432_.isClientSide)
        {
            ItemStack heldItem = p_41433_.getItemInHand(p_41434_);
            if(p_41433_.isCrouching())
            {
                heldItem.resetHoverName();
                heldItem.setTag(null);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
            }
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level LevelIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        if(stack.hasTag())
        {
            CompoundTag tag = stack.getTag();
            if(tag != null)
            {
                tooltip.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "ID: " + ChatFormatting.RESET + tag.getUUID("id")));
                tooltip.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Device: " + ChatFormatting.RESET + tag.getString("name")));

                BlockPos devicePos = BlockPos.of(tag.getLong("pos"));
                String builder = ChatFormatting.RED.toString() + ChatFormatting.BOLD + "X: " + ChatFormatting.RESET + devicePos.getX() + " " +
                        ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Y: " + ChatFormatting.RESET + devicePos.getY() + " " +
                        ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Z: " + ChatFormatting.RESET + devicePos.getZ();
                tooltip.add(Component.literal(builder));
            }
        }
        else
        {
            if(!Screen.hasShiftDown())
            {
                tooltip.add(Component.literal(ChatFormatting.GRAY + "Use this cable to connect"));
                tooltip.add(Component.literal(ChatFormatting.GRAY + "a device to a router."));
                tooltip.add(Component.literal(ChatFormatting.YELLOW + "Hold SHIFT for How-To"));
                return;
            }

            tooltip.add(Component.literal(ChatFormatting.GRAY + "Start by right clicking a"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "device with this cable"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "then right click the "));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "router you want to"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "connect this device to."));
        }
        super.appendHoverText(stack, LevelIn, tooltip, flagIn);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return p_41453_.hasTag();
    }

    private static double getDistance(BlockPos source, BlockPos target)
    {
        return Math.sqrt(source.distToCenterSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5));
    }


    @Override
    public Component getName(ItemStack p_41458_) {
        if(p_41458_.hasTag())
        {
            return Component.literal(ChatFormatting.GRAY.toString() + ChatFormatting.BOLD + super.getName(p_41458_));
        }
        return super.getName(p_41458_);
    }
}
