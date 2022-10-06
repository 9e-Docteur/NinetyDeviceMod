package com.mrcrayfish.device.event;

import com.mrcrayfish.device.api.utils.BankUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.IOException;

public class BankEvents 
{
	@SubscribeEvent
	public void load(LevelEvent.Load event)
	{
		{
			try 
			{
				File data = new File("bank.dat");
				if(!data.exists())
				{
					return;
				}
				
				CompoundTag nbt = NbtIo.read(data);
				if(nbt != null)
				{
					BankUtil.INSTANCE.load(nbt);
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
				File data = new File("bank.dat");
				if(!data.exists())
				{
					data.createNewFile();
				}
				
				CompoundTag nbt = new CompoundTag();
				BankUtil.INSTANCE.save(nbt);
				NbtIo.write(nbt, data);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
