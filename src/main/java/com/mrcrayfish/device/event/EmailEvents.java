package com.mrcrayfish.device.event;

import com.mrcrayfish.device.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.IOException;

public class EmailEvents 
{
	@SubscribeEvent
	public void load(LevelEvent.Load event)
	{
		{
			try 
			{
				File data = new File("emails.dat");
				if(!data.exists())
				{
					return;
				}
				
				CompoundTag nbt = NbtIo.read(data);
				if(nbt != null)
				{
					EmailManager.INSTANCE.readFromNBT(nbt);
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void save(LevelEvent.Save event)
	{
		{
			try 
			{
				File data = new File("emails.dat");
				if(!data.exists())
				{
					data.createNewFile();
				}
				
				CompoundTag nbt = new CompoundTag();
				EmailManager.INSTANCE.writeToNBT(nbt);
				NbtIo.write(nbt, data);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
