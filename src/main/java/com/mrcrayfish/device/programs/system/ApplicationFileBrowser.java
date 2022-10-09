package com.mrcrayfish.device.programs.system;


import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class ApplicationFileBrowser extends SystemApplication
{
	private FileBrowser browser;
	
	public ApplicationFileBrowser()
	{
		this.setDefaultWidth(211);
		this.setDefaultHeight(145);
	}

	@Override
	public void init(@Nullable CompoundTag intent)
	{
		browser = new FileBrowser(0, 0, this, FileBrowser.Mode.FULL);
		browser.openFolder(FileSystem.DIR_HOME);
		this.addComponent(browser);
	}

	@Override
	public void load(CompoundTag tagCompound)
	{

	}

	@Override
	public void save(CompoundTag tagCompound) 
	{
		
	}

	public static class FileBrowserTrayItem extends TrayItem
	{
		public FileBrowserTrayItem()
		{
			super(Icons.FOLDER);
		}

		@Override
		public void handleClick(int mouseX, int mouseY, int mouseButton)
		{
			AppInfo info = ApplicationManager.getApplication("ndm:file_browser");
			if(info != null)
			{
				Laptop.getSystem().openApplication(info);
			}
		}
	}
}
