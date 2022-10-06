package com.mrcrayfish.device.programs.email.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskSendEmail extends Task 
{
	private Email email;
	private String to;
	
	public TaskSendEmail() 
	{
		super("send_email");
	}
	
	public TaskSendEmail(Email email, String to)
	{
		this();
		this.email = email;
		this.to = to;
	}

	@Override
	public void prepareRequest(CompoundTag nbt) 
	{
		this.email.writeToNBT(nbt);
		nbt.putString("to", this.to);
	}

	@Override
	public void processRequest(CompoundTag nbt, Level Level, Player player)
	{
		String name = EmailManager.INSTANCE.getName(player);
		if(name != null)
		{
			Email email = Email.readFromNBT(nbt);
			email.setAuthor(name);
			if(EmailManager.INSTANCE.addEmailToInbox(email, nbt.getString("to"))) 
			{
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(CompoundTag nbt) {}

	@Override
	public void processResponse(CompoundTag nbt) {}
	
}
