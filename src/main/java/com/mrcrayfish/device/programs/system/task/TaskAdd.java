package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskAdd extends Task 
{
	private int amount;
	
	public TaskAdd()
	{
		super("bank_add");
	}
	
	public TaskAdd(int amount)
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
		Account sender = BankUtil.INSTANCE.getAccount(player);
		sender.add(amount);
		this.setSuccessful();
	}

	@Override
	public void prepareResponse(CompoundTag nbt) 
	{
		nbt.putInt("balance", this.amount);
	}

	@Override
	public void processResponse(CompoundTag nbt) {}
}
