package com.mrcrayfish.device.programs.system.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TaskPay extends Task 
{
	private String uuid;
	private int amount;
	
	public TaskPay()
	{
		super("bank_pay");
	}
	
	public TaskPay(String uuid, int amount)
	{
		this();
		this.uuid = uuid;
		this.amount = amount;
	}

	@Override
	public void prepareRequest(CompoundTag nbt)
	{
		nbt.putString("player", this.uuid);
		nbt.putInt("amount", this.amount);
	}

	@Override
	public void processRequest(CompoundTag nbt, Level Level, Player player)
	{
		String uuid = nbt.getString("uuid");
		int amount = nbt.getInt("amount");
		Account sender = BankUtil.INSTANCE.getAccount(player);
		Account recipient = BankUtil.INSTANCE.getAccount(UUID.fromString(uuid));
		if(recipient != null && sender.hasAmount(amount)) {
			recipient.add(amount);
			sender.remove(amount);
			this.amount = sender.getBalance();
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
