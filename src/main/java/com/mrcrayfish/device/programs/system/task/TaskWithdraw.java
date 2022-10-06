package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class TaskWithdraw extends Task
{
    private int amount;

    private TaskWithdraw()
    {
        super("bank_withdraw");
    }

    public TaskWithdraw(int amount)
    {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundTag nbt)
    {
        nbt.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level Level, Player player)
    {
        int amount = nbt.getInt("amount");
        Account account = BankUtil.INSTANCE.getAccount(player);
        if(account.withdraw(amount))
        {
            int stacks = amount / 64;
            for(int i = 0; i < stacks; i++)
            {
                Level.addFreshEntity(new ItemEntity(Level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, 64)));
            }

            int remaining = amount % 64;
            if(remaining > 0)
            {
                Level.addFreshEntity(new ItemEntity(Level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, remaining)));
            }

            this.amount = account.getBalance();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt)
    {
        nbt.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(CompoundTag nbt) {}
}
