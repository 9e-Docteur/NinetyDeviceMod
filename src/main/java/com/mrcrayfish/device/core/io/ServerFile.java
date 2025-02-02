package com.mrcrayfish.device.core.io;

import com.mrcrayfish.device.api.app.Application;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Comparator;

/**
 * Author: MrCrayfish
 */
public class ServerFile
{
    public static final Comparator<ServerFile> SORT_BY_NAME = (f1, f2) -> {
        if(f1.isFolder() && !f2.isFolder()) return -1;
        if(!f1.isFolder() && f2.isFolder()) return 1;
        return f1.name.compareTo(f2.name);
    };

    protected ServerFolder parent;
    protected String name;
    protected String openingApp;
    protected CompoundTag data;
    protected boolean protect = false;

    protected ServerFile() {}

    public ServerFile(String name, Application app, CompoundTag data)
    {
        this(name, app.getInfo().getFormattedId(), data, false);
    }

    public ServerFile(String name, String openingAppId, CompoundTag data)
    {
        this(name, openingAppId, data, false);
    }

    private ServerFile(String name, String openingAppId, CompoundTag data, boolean protect)
    {
        this.name = name;
        this.openingApp = openingAppId;
        this.data = data;
        this.protect = protect;
    }

    public String getName()
    {
        return name;
    }

    public FileSystem.Response rename(String name)
    {
        if(this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot rename a protected file");

        if(!FileSystem.PATTERN_FILE_NAME.matcher(name).matches())
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name");

        this.name = name;
        return FileSystem.createSuccessResponse();
    }

    @Nullable
    public String getOpeningApp()
    {
        return openingApp;
    }

    public FileSystem.Response setData(CompoundTag data)
    {
        if(this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot set data on a protected file");

        if(data == null)
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID_DATA, "Invalid data");

        this.data = data;
        return FileSystem.createSuccessResponse();
    }

    @Nullable
    public CompoundTag getData()
    {
        return data;
    }

    @Nullable
    public ServerFolder getParent()
    {
        return parent;
    }

    public boolean isProtected()
    {
        return protect;
    }

    public boolean isFolder()
    {
        return false;
    }

    public boolean isForApplication(Application app)
    {
        return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
    }

    public FileSystem.Response delete()
    {
        if(this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete a protected file");

        if(parent != null)
            return parent.delete(this);

        return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Invalid file");
    }

    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("openingApp", openingApp);
        tag.put("data", data);
        return tag;
    }

    public static ServerFile fromTag(String name, CompoundTag tag)
    {
        return new ServerFile(name, tag.getString("openingApp"), tag.getCompound("data"));
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof ServerFile))
            return false;
        return ((ServerFile) obj).name.equalsIgnoreCase(name);
    }

    public ServerFile copy()
    {
        return new ServerFile(name, openingApp, data.copy());
    }
}
