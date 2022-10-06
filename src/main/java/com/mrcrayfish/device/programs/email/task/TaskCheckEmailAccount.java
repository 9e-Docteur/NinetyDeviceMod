package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskCheckEmailAccount extends Task 
{
	private boolean hasAccount = false;
	private String name = null;
	
	public TaskCheckEmailAccount()
	{
		super("check_email_account");
	}

	@Override
	public void prepareRequest(CompoundTag nbt) {}

	@Override
	public void processRequest(CompoundTag nbt, Level Level, Player player)
	{
		this.hasAccount = EmailManager.INSTANCE.hasAccount(player.getUUID());
		if(this.hasAccount)
		{
			this.name = EmailManager.INSTANCE.getName(player);
			this.setSuccessful();
		}
	}

	@Override
	public void prepareResponse(CompoundTag nbt) 
	{
		if(this.isSucessful()) nbt.putString("Name", this.name);
	}

	@Override
	public void processResponse(CompoundTag nbt) {}

}
