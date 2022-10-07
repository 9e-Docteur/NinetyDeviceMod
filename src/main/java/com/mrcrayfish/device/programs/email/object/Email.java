package com.mrcrayfish.device.programs.email.object;

import com.mrcrayfish.device.api.io.File;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.FormattedText;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class Email
{
    private final String subject;
    private String author;
    private final FormattedText message;
    private final File attachment;
    private boolean read;

    public Email(String subject, FormattedText message, @Nullable File file)
    {
        this.subject = subject;
        this.message = message;
        this.attachment = file;
        this.read = false;
    }

    public Email(String subject, String author, FormattedText message, @Nullable File attachment)
    {
        this(subject, message, attachment);
        this.author = author;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public FormattedText getMessage()
    {
        return message;
    }

    public File getAttachment()
    {
        return attachment;
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    public void writeToNBT(CompoundTag nbt)
    {
        nbt.putString("subject", this.subject);
        if(author != null) nbt.putString("author", this.author);
        nbt.putString("message", String.valueOf(this.message));
        nbt.putBoolean("read", this.read);

        if(attachment != null)
        {
            CompoundTag fileTag = new CompoundTag();
            fileTag.putString("file_name", attachment.getName());
            fileTag.put("data", attachment.toTag());
            nbt.put("attachment", fileTag);
        }
    }

    public static Email readFromNBT(CompoundTag nbt)
    {
        File attachment = null;
        if(nbt.contains("attachment", Tag.TAG_COMPOUND))
        {
            CompoundTag fileTag = nbt.getCompound("attachment");
            attachment = File.fromTag(fileTag.getString("file_name"), fileTag.getCompound("data"));
        }
        Email email = new Email(nbt.getString("subject"), nbt.getString("author"), FormattedText.of(nbt.getString("message")), attachment);
        email.setRead(nbt.getBoolean("read"));
        return email;
    }
}
