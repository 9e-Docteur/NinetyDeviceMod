package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.Folder;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.action.FileAction;
import com.mrcrayfish.device.core.io.drive.AbstractDrive;
import com.mrcrayfish.device.core.io.drive.ExternalDrive;
import com.mrcrayfish.device.core.io.drive.InternalDrive;
import com.mrcrayfish.device.core.io.task.TaskGetFiles;
import com.mrcrayfish.device.core.io.task.TaskGetMainDrive;
import com.mrcrayfish.device.core.io.task.TaskSendAction;
import com.mrcrayfish.device.init.DeviceItems;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class FileSystem
{
	public static final Pattern PATTERN_FILE_NAME = Pattern.compile("^[\\w'. ]{1,32}$");
	public static final Pattern PATTERN_DIRECTORY = Pattern.compile("^(/)|(/[\\w'. ]{1,32})*$");

	public static final String DIR_ROOT = "/";
	public static final String DIR_APPLICATION_DATA = DIR_ROOT + "Application Data";
	public static final String DIR_HOME = DIR_ROOT + "Home";
	public static final String LAPTOP_DRIVE_NAME = "Root";

	private AbstractDrive mainDrive = null;
	private final Map<UUID, AbstractDrive> additionalDrives = new HashMap<>();
	private AbstractDrive attachedDrive = null;
	private DyeColor attachedDriveColor = DyeColor.RED;

	private final TileEntityLaptop tileEntity;
	
	public FileSystem(TileEntityLaptop tileEntity, CompoundTag fileSystemTag)
	{
		this.tileEntity = tileEntity;

		load(fileSystemTag);
	}

	private void load(CompoundTag fileSystemTag)
	{
		if(fileSystemTag.contains("main_drive", Tag.TAG_COMPOUND))
		{
			mainDrive = InternalDrive.fromTag(fileSystemTag.getCompound("main_drive"));
		}

		if(fileSystemTag.contains("drives", Tag.TAG_LIST))
		{
			ListTag tagList = fileSystemTag.getList("drives", Tag.TAG_COMPOUND);
			for(int i = 0; i < tagList.size(); i++)
			{
				CompoundTag driveTag = tagList.getCompound(i);
				AbstractDrive drive = InternalDrive.fromTag(driveTag.getCompound("drive"));
				additionalDrives.put(drive.getUUID(), drive);
			}
		}

		if(fileSystemTag.contains("external_drive", Tag.TAG_COMPOUND))
		{
			attachedDrive = ExternalDrive.fromTag(fileSystemTag.getCompound("external_drive"));
		}

		if(fileSystemTag.contains("external_drive_color", Tag.TAG_BYTE))
		{
			attachedDriveColor = DyeColor.byId(fileSystemTag.getByte("external_drive_color"));
		}

		setupDefault();
	}

	/**
	 * Sets up the default folders for the file system if they don't exist.
	 */
	private void setupDefault()
	{
		if(mainDrive == null)
		{
			AbstractDrive drive = new InternalDrive(LAPTOP_DRIVE_NAME);
			ServerFolder root = drive.getRoot(tileEntity.getLevel());
			root.add(createProtectedFolder("Home"), false);
			root.add(createProtectedFolder("Application Data"), false);
			mainDrive = drive;
			tileEntity.setChanged();
		}
	}

	private ServerFolder createProtectedFolder(String name)
	{
		try
		{
			Constructor<ServerFolder> constructor = ServerFolder.class.getDeclaredConstructor(String.class, boolean.class);
			constructor.setAccessible(true);
			return constructor.newInstance(name, true);
		}
		catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendAction(Drive drive, FileAction action, @Nullable Callback<Response> callback)
	{
		if(Laptop.getPos() != null)
		{
			Task task = new TaskSendAction(drive, action);
			task.setCallback((nbt, success) ->
			{
				if(callback != null)
				{
					callback.execute(Response.fromTag(nbt.getCompound("response")), success);
				}
            });
			TaskManager.sendTask(task);
		}
	}

	public Response readAction(String driveUuid, FileAction action, Level Level)
	{
		UUID uuid = UUID.fromString(driveUuid);
		AbstractDrive drive = getAvailableDrives(Level, true).get(uuid);
		if(drive != null)
		{
			Response response = drive.handleFileAction(this, action, Level);
			if(response.getStatus() == Status.SUCCESSFUL)
			{
				tileEntity.setChanged();
			}
			return response;
		}
		return createResponse(Status.DRIVE_UNAVAILABLE, "Drive unavailable or missing");
	}

	public AbstractDrive getMainDrive()
	{
		return mainDrive;
	}

	public Map<UUID, AbstractDrive> getAvailableDrives(Level Level, boolean includeMain)
	{
		Map<UUID, AbstractDrive> drives = new LinkedHashMap<>();

		if(includeMain)
			drives.put(mainDrive.getUUID(), mainDrive);

		additionalDrives.forEach(drives::put);

		if(attachedDrive != null)
			drives.put(attachedDrive.getUUID(), attachedDrive);

		//TODO add network drives
		return drives;
	}

	public boolean setAttachedDrive(ItemStack flashDrive)
	{
		if(attachedDrive == null)
		{
			CompoundTag flashDriveTag = getExternalDriveTag(flashDrive);
			AbstractDrive drive = ExternalDrive.fromTag(flashDriveTag.getCompound("drive"));
			if(drive != null)
			{
				drive.setName(String.valueOf(flashDrive.getDisplayName()));
				attachedDrive = drive;
				attachedDriveColor = DyeColor.getColor(flashDrive.copy());

				tileEntity.getPipeline().putByte("external_drive_color", (byte) attachedDriveColor.getId());
				tileEntity.sync();

				return true;
			}
		}
		return false;
	}

	public AbstractDrive getAttachedDrive()
	{
		return attachedDrive;
	}

	public DyeColor getAttachedDriveColor()
	{
		return attachedDriveColor;
	}

	@Nullable
	public ItemStack removeAttachedDrive()
	{
		if(attachedDrive != null)
		{
//			ItemStack stack = new ItemStack(DeviceItems.FLASH_DRIVE.get(), 1, getAttachedDriveColor().getId());
//			stack.setHoverName(Component.literal(attachedDrive.getName()));
//			stack.getTag().put("drive", attachedDrive.toTag());
//			attachedDrive = null;
//			return stack;
		}
		return null;
	}

	private CompoundTag getExternalDriveTag(ItemStack stack)
	{
		CompoundTag tagCompound = stack.getTag();
		if(tagCompound == null)
		{
			tagCompound = new CompoundTag();
			tagCompound.put("drive", new ExternalDrive(stack.getDisplayName()).toTag());
			stack.setTag(tagCompound);
		}
		else if(!tagCompound.contains("drive", Tag.TAG_COMPOUND))
		{
			tagCompound.put("drive", new ExternalDrive(stack.getDisplayName()).toTag());
		}
		return tagCompound;
	}

	public static void getApplicationFolder(Application app, Callback<Folder> callback)
	{
		if(MrCrayfishDeviceMod.proxy.hasAllowedApplications())
		{
			if(!MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(app.getInfo()))
			{
				callback.execute(null, false);
				return;
			}
		}
		if(Laptop.getMainDrive() == null)
		{
			Task task = new TaskGetMainDrive(Laptop.getPos());
			task.setCallback((nbt, success) ->
			{
				if(success)
				{
					setupApplicationFolder(app, callback);
				}
				else
				{
					callback.execute(null, false);
				}
			});
			TaskManager.sendTask(task);
		}
		else
		{
			setupApplicationFolder(app, callback);
		}
	}

	private static void setupApplicationFolder(Application app, Callback<Folder> callback)
	{
		Folder folder = Laptop.getMainDrive().getFolder(FileSystem.DIR_APPLICATION_DATA);
		if(folder != null)
		{
			if(folder.hasFolder(app.getInfo().getFormattedId()))
			{
				Folder appFolder = folder.getFolder(app.getInfo().getFormattedId());
				if(appFolder.isSynced())
				{
					callback.execute(appFolder, true);
				}
				else
				{
					Task task = new TaskGetFiles(appFolder, Laptop.getPos());
					task.setCallback((nbt, success) ->
					{
						if(success && nbt.contains("files", Tag.TAG_LIST))
						{
							ListTag files = nbt.getList("files", Tag.TAG_COMPOUND);
							appFolder.syncFiles(files);
							callback.execute(appFolder, true);
						}
						else
						{
							callback.execute(null, false);
						}
					});
					TaskManager.sendTask(task);
				}
			}
			else
			{
				Folder appFolder = new Folder(app.getInfo().getFormattedId());
				folder.add(appFolder, (response, success) ->
				{
					if(response != null && response.getStatus() == Status.SUCCESSFUL)
					{
						callback.execute(appFolder, true);
					}
					else
					{
						callback.execute(null, false);
					}
				});
			}
		}
		else
		{
			callback.execute(null, false);
		}
	}

	public CompoundTag toTag()
	{
		CompoundTag fileSystemTag = new CompoundTag();

		if(mainDrive != null)
			fileSystemTag.put("main_drive", mainDrive.toTag());

		ListTag tagList = new ListTag();
		additionalDrives.forEach((k, v) -> tagList.add(v.toTag()));
		fileSystemTag.put("drives", tagList);

		if(attachedDrive != null)
		{
			fileSystemTag.put("external_drive", attachedDrive.toTag());
			fileSystemTag.putByte("external_drive_color", (byte) attachedDriveColor.getId());
		}

		return fileSystemTag;
	}

	public static Response createSuccessResponse()
	{
		return new Response(Status.SUCCESSFUL);
	}

	public static Response createResponse(int status, String message)
	{
		return new Response(status, message);
	}

	public static class Response
	{
		private final int status;
		private String message = "";

		private Response(int status)
		{
			this.status = status;
		}

		private Response(int status, String message)
		{
			this.status = status;
			this.message = message;
		}

		public int getStatus()
		{
			return status;
		}

		public String getMessage()
		{
			return message;
		}

		public CompoundTag toTag()
		{
			CompoundTag responseTag = new CompoundTag();
			responseTag.putInt("status", status);
			responseTag.putString("message", message);
			return responseTag;
		}

		public static Response fromTag(CompoundTag responseTag)
		{
			return new Response(responseTag.getInt("status"), responseTag.getString("message"));
		}
	}

	public static class Status
	{
		public static final int FAILED = 0;
		public static final int SUCCESSFUL = 1;
		public static final int FILE_INVALID = 2;
		public static final int FILE_IS_PROTECTED = 3;
		public static final int FILE_EXISTS = 4;
		public static final int FILE_INVALID_NAME = 5;
		public static final int FILE_INVALID_DATA = 6;
		public static final int DRIVE_UNAVAILABLE = 7;
	}
}
