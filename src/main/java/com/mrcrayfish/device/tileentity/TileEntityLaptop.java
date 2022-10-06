package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.util.TileEntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityLaptop extends TileEntityNetworkDevice.Colored
{
	private static final int OPENED_ANGLE = 102;

	private boolean open = false;

	private CompoundTag applicationData;
	private CompoundTag systemData;
	private FileSystem fileSystem;

	@OnlyIn(Dist.CLIENT)
	private int rotation;

	@OnlyIn(Dist.CLIENT)
	private int prevRotation;

	@OnlyIn(Dist.CLIENT)
	private DyeColor externalDriveColor;
	private final Level level = Minecraft.getInstance().level;

	public TileEntityLaptop(BlockPos p_155229_, BlockState p_155230_) {
		super(DeviceTileEntites.LAPTOP.get(), p_155229_, p_155230_);
	}

	@Override
	public String getDeviceName()
	{
		return "Laptop";
	}


	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide)
		{
			prevRotation = rotation;
			if(!open)
			{
				if(rotation > 0)
				{
					rotation -= 10F;
				}
			}
			else
			{
				if(rotation < OPENED_ANGLE)
				{
					rotation += 10F;
				}
			}
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		super.serializeNBT();
		CompoundTag compound = new CompoundTag();
		compound.putBoolean("open", open);

		if(systemData != null)
		{
			compound.put("system_data", systemData);
		}

		if(applicationData != null)
		{
			compound.put("application_data", applicationData);
		}

		if(fileSystem != null)
		{
			compound.put("file_system", fileSystem.toTag());
		}
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundTag compound) {
		super.deserializeNBT(compound);
		if(compound.contains("open"))
		{
			this.open = compound.getBoolean("open");
		}
		if(compound.contains("system_data", Tag.TAG_COMPOUND))
		{
			this.systemData = compound.getCompound("system_data");
		}
		if(compound.contains("application_data", Tag.TAG_COMPOUND))
		{
			this.applicationData = compound.getCompound("application_data");
		}
		if(compound.contains("file_system"))
		{
			this.fileSystem = new FileSystem(this, compound.getCompound("file_system"));
		}
		if(compound.contains("external_drive_color", Tag.TAG_BYTE))
		{
			this.externalDriveColor = null;
			if(compound.getByte("external_drive_color") != -1)
			{
				this.externalDriveColor = DyeColor.byId(compound.getByte("external_drive_color"));
			}
		}
	}
	@Override
	public CompoundTag writeSyncTag()
	{
		CompoundTag tag = super.writeSyncTag();
		tag.putBoolean("open", open);
		tag.put("system_data", getSystemData());

		if(getFileSystem().getAttachedDrive() != null)
		{
			tag.putByte("external_drive_color", (byte) getFileSystem().getAttachedDriveColor().getId());
		}
		else
		{
			tag.putByte("external_drive_color", (byte) -1);
		}

		return tag;
	}

	public void openClose()
	{
		open = !open;
		pipeline.putBoolean("open", open);
		sync();
	}

	public boolean isOpen()
	{
		return open;
	}

	public CompoundTag getApplicationData()
    {
		return applicationData != null ? applicationData : new CompoundTag();
    }

	public CompoundTag getSystemData()
	{
		if(systemData == null)
		{
			systemData = new CompoundTag();
		}
		return systemData;
	}

	public FileSystem getFileSystem()
	{
		if(fileSystem == null)
		{
			fileSystem = new FileSystem(this, new CompoundTag());
		}
		return fileSystem;
	}

	public void setApplicationData(String appId, CompoundTag applicationData)
	{
		this.applicationData = applicationData;
		setChanged();
		setChanged(level, worldPosition, getBlockState());
	}

	public void setSystemData(CompoundTag systemData)
	{
		this.systemData = systemData;
		setChanged();
		setChanged(level, worldPosition, getBlockState());
	}

	@OnlyIn(Dist.CLIENT)
	public float getScreenAngle(float partialTicks)
	{
		return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isExternalDriveAttached()
	{
		return externalDriveColor != null;
	}

	@OnlyIn(Dist.CLIENT)
	public DyeColor getExternalDriveColor()
	{
		return externalDriveColor;
	}

	@Override
	public DyeColor getColor() {
		return null;
	}

	@Override
	public void setColor(DyeColor color) {

	}
}
