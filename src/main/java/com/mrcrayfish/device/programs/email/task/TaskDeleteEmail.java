package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class TaskDeleteEmail extends Task {
	
	private int index;
	
	public TaskDeleteEmail() 
	{
		super("delete_email");
	}
	
	public TaskDeleteEmail(int index) 
	{
		this();
		this.index = index;
	}

	@Override
	public void prepareRequest(CompoundTag nbt) 
	{
		nbt.putInt("Index", this.index);
	}

	@Override
	public void processRequest(CompoundTag nbt, Level Level, Player player)
	{
		List<Email> emails = EmailManager.INSTANCE.getEmailsForAccount(player);
		if(emails != null)
		{
			int index = nbt.getInt("Index");
			if(index >= 0 && index < emails.size())
			{
				emails.remove(index);
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(CompoundTag nbt) {}

	@Override
	public void processResponse(CompoundTag nbt) {}
}
