package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import com.mrcrayfish.device.util.InventoryUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class TaskDeposit extends Task
{
    private int amount;

    protected TaskDeposit()
    {
        super("bank_deposit");
    }

    public TaskDeposit(int amount)
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
        Account account = BankUtil.INSTANCE.getAccount(player);
        int amount = nbt.getInt("amount");
        long value = account.getBalance() + amount;
        if(value > Integer.MAX_VALUE)
        {
            amount = Integer.MAX_VALUE - account.getBalance();
        }
        if(InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount))
        {
            if(account.deposit(amount))
            {
                this.amount = account.getBalance();
                this.setSuccessful();
            }
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
