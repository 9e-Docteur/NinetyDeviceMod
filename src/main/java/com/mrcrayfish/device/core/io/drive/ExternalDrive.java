package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.ServerFolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive
{
    private static final Predicate<CompoundTag> PREDICATE_DRIVE_TAG = tag ->
            tag.contains("name", Tag.TAG_STRING)
            && tag.contains("uuid", Tag.TAG_STRING)
            && tag.contains("root", Tag.TAG_COMPOUND);

    private ExternalDrive() {}

    public ExternalDrive(Component displayName)
    {
        super(String.valueOf(Component.literal(String.valueOf(displayName))));
    }

    @Nullable
    public static AbstractDrive fromTag(CompoundTag driveTag)
    {
        if(!PREDICATE_DRIVE_TAG.test(driveTag))
            return null;

        AbstractDrive drive = new ExternalDrive();
        drive.name = driveTag.getString("name");
        drive.uuid = UUID.fromString(driveTag.getString("uuid"));

        CompoundTag folderTag = driveTag.getCompound("root");
        drive.root = ServerFolder.fromTag(folderTag.getString("file_name"), folderTag.getCompound("data"));

        return drive;
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag driveTag = new CompoundTag();
        driveTag.putString("name", name);
        driveTag.putString("uuid", uuid.toString());

        CompoundTag folderTag = new CompoundTag();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType()
    {
        return Type.EXTERNAL;
    }
}
