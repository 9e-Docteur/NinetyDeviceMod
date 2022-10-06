package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.block.BlockPrinter;
import com.mrcrayfish.device.init.DeviceSounds;
import com.mrcrayfish.device.init.DeviceTileEntites;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.mrcrayfish.device.tileentity.TileEntityPrinter.State.*;

/**
 * Author: MrCrayfish
 */
public class TileEntityPrinter extends TileEntityNetworkDevice.Colored
{
    private State state = IDLE;

    private final Deque<IPrint> printQueue = new ArrayDeque<>();
    private IPrint currentPrint;

    private int totalPrintTime;
    private int remainingPrintTime;
    private int paperCount = 0;
    private final Level level = Minecraft.getInstance().level;

    public TileEntityPrinter(BlockPos p_155229_, BlockState p_155230_) {
        super(DeviceTileEntites.PRINTER.get(), p_155229_, p_155230_);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide)
        {
            if(remainingPrintTime > 0)
            {
                if(remainingPrintTime % 20 == 0 || state == LOADING_PAPER)
                {
                    pipeline.putInt("remainingPrintTime", remainingPrintTime);
                    sync();
                    if(remainingPrintTime != 0 && state == PRINTING)
                    {
                        level.playSound(null, worldPosition, DeviceSounds.PRINTER_PRINTING, SoundSource.BLOCKS, 0.5F, 1.0F);
                    }
                }
                remainingPrintTime--;
            }
            else
            {
                setState(state.next());
            }
        }

        if(state == IDLE && remainingPrintTime == 0 && currentPrint != null)
        {
            if(!level.isClientSide)
            {
                BlockState state = level.getBlockState(worldPosition);
                ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 0.0625, worldPosition.getZ(), IPrint.generateItem(currentPrint));
                entity.xo = 0;
                entity.yo = 0;
                entity.zo = 0;
                level.addFreshEntity(entity);
            }
            currentPrint = null;
        }

        if(state == IDLE && currentPrint == null && !printQueue.isEmpty() && paperCount > 0)
        {
            print(printQueue.poll());
        }
    }

    @Override
    public String getDeviceName()
    {
        return "Printer";
    }


    @Override
    public CompoundTag serializeNBT() {
        super.serializeNBT();
        CompoundTag compound = new CompoundTag();
        compound.putInt("totalPrintTime", totalPrintTime);
        compound.putInt("remainingPrintTime", remainingPrintTime);
        compound.putInt("state", state.ordinal());
        compound.putInt("paperCount", paperCount);
        if(currentPrint != null)
        {
            compound.put("currentPrint", IPrint.writeToTag(currentPrint));
        }
        if(!printQueue.isEmpty())
        {
            ListTag queue = new ListTag();
            printQueue.forEach(print -> {
                queue.add(IPrint.writeToTag(print));
            });
            compound.put("queue", queue);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        if(compound.contains("currentPrint", Tag.TAG_COMPOUND))
        {
            currentPrint = IPrint.loadFromTag(compound.getCompound("currentPrint"));
        }
        if(compound.contains("totalPrintTime", Tag.TAG_INT))
        {
            totalPrintTime = compound.getInt("totalPrintTime");
        }
        if(compound.contains("remainingPrintTime", Tag.TAG_INT))
        {
            remainingPrintTime = compound.getInt("remainingPrintTime");
        }
        if(compound.contains("state", Tag.TAG_INT))
        {
            state = State.values()[compound.getInt("state")];
        }
        if(compound.contains("paperCount", Tag.TAG_INT))
        {
            paperCount = compound.getInt("paperCount");
        }
        if(compound.contains("queue", Tag.TAG_LIST))
        {
            printQueue.clear();
            ListTag queue = compound.getList("queue", Tag.TAG_COMPOUND);
            for(int i = 0; i < queue.size(); i++)
            {
                IPrint print = IPrint.loadFromTag((CompoundTag) queue.get(i));
                printQueue.offer(print);
            }
        }
    }
    @Override
    public CompoundTag writeSyncTag()
    {
        CompoundTag tag = super.writeSyncTag();
        tag.putInt("paperCount", paperCount);
        return tag;
    }

    public void setState(State newState)
    {
        if(newState == null)
            return;

        state = newState;
        if(state == PRINTING)
        {
            if(DeviceConfig.OVERRIDE_PRINT_SPEED.get())
            {
                remainingPrintTime = DeviceConfig.CUSTOM_PRINT_SPEED.get() * 20;
            }
            else
            {
                remainingPrintTime = currentPrint.speed() * 20;
            }
        }
        else
        {
            remainingPrintTime = state.animationTime;
        }
        totalPrintTime = remainingPrintTime;

        pipeline.putInt("state", state.ordinal());
        pipeline.putInt("totalPrintTime", totalPrintTime);
        pipeline.putInt("remainingPrintTime", remainingPrintTime);
        sync();
    }

    public void addToQueue(IPrint print)
    {
        printQueue.offer(print);
    }

    private void print(IPrint print)
    {
        level.playSound(null, worldPosition, DeviceSounds.PRINTER_LOADING_PAPER, SoundSource.BLOCKS, 0.5F, 1.0F);

        setState(LOADING_PAPER);
        currentPrint = print;
        paperCount--;

        pipeline.putInt("paperCount", paperCount);
        pipeline.put("currentPrint", IPrint.writeToTag(currentPrint));
        sync();
    }

    public boolean isLoading()
    {
        return state == LOADING_PAPER;
    }

    public boolean isPrinting()
    {
        return state == PRINTING;
    }

    public int getTotalPrintTime()
    {
        return totalPrintTime;
    }

    public int getRemainingPrintTime()
    {
        return remainingPrintTime;
    }

    public boolean addPaper(ItemStack stack, boolean addAll)
    {
        if(!stack.isEmpty() && stack.getItem() == Items.PAPER && paperCount < DeviceConfig.MAX_PAPER_COUNT.get())
        {
            if(!addAll)
            {
                paperCount++;
                stack.shrink(1);
            }
            else
            {
                paperCount += stack.getCount();
                stack.setCount(Math.max(0, paperCount - 64));
                paperCount = Math.min(64, paperCount);
            }
            pipeline.putInt("paperCount", paperCount);
            sync();
            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    public boolean hasPaper()
    {
        return paperCount > 0;
    }

    public int getPaperCount()
    {
        return paperCount;
    }

    public IPrint getPrint()
    {
        return currentPrint;
    }

    @Override
    public DyeColor getColor() {
        return null;
    }

    @Override
    public void setColor(DyeColor color) {
        
    }

    public enum State
    {
        LOADING_PAPER(30), PRINTING(0), IDLE(0);

        final int animationTime;

        State(int time)
        {
            this.animationTime = time;
        }

        public State next()
        {
            if(ordinal() + 1 >= values().length)
                return null;
            return values()[ordinal() + 1];
        }
    }
}
