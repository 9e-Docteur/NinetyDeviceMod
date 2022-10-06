package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskRemove extends Task 
{
	private int amount;
	
	public TaskRemove()
	{
		super("bank_remove");
	}
	
	public TaskRemove(int amount)
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
		this.amount = nbt.getInt("amount");
		Account sender = BankUtil.INSTANCE.getAccount(player);
		if(sender.hasAmount(amount))
		{
			sender.remove(amount);
			this.setSuccessful();
		}
	}

	@Override
	public void prepareResponse(CompoundTag nbt) 
	{
		if(isSucessful())
		{
			nbt.putInt("balance", this.amount);
		}
	}

	@Override
	public void processResponse(CompoundTag nbt) {}
}
