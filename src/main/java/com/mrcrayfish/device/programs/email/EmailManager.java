package com.mrcrayfish.device.programs.email;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.programs.email.object.Email;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

/**
 * Author: MrCrayfish
 */
public class EmailManager
{
    public static final EmailManager INSTANCE = new EmailManager();

    @OnlyIn(Dist.CLIENT)
    private List<Email> inbox;

    private final HashBiMap<UUID, String> uuidToName = HashBiMap.create();
    private final Map<String, List<Email>> nameToInbox = new HashMap<>();

    public boolean addEmailToInbox(Email email, String to)
    {
        if (nameToInbox.containsKey(to))
        {
            nameToInbox.get(to).add(0, email);
            sendNotification(to, email);
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Email> getInbox()
    {
        if(inbox == null)
        {
            inbox = new ArrayList<>();
        }
        return inbox;
    }

    public List<Email> getEmailsForAccount(Player player)
    {
        if (uuidToName.containsKey(player.getUUID()))
        {
            return nameToInbox.get(uuidToName.get(player.getUUID()));
        }
        return new ArrayList<Email>();
    }

    public boolean addAccount(Player player, String name)
    {
        if (!uuidToName.containsKey(player.getUUID()))
        {
            if (!uuidToName.containsValue(name))
            {
                uuidToName.put(player.getUUID(), name);
                nameToInbox.put(name, new ArrayList<Email>());
                return true;
            }
        }
        return false;
    }

    public boolean hasAccount(UUID uuid)
    {
        return uuidToName.containsKey(uuid);
    }

    public String getName(Player player)
    {
        return uuidToName.get(player.getUUID());
    }

    public void readFromNBT(CompoundTag nbt)
    {
        nameToInbox.clear();

        ListTag inboxes = (ListTag) nbt.get("Inboxes");
        for (int i = 0; i < inboxes.size(); i++)
        {
            CompoundTag inbox = inboxes.getCompound(i);
            String name = inbox.getString("Name");

            List<Email> emails = new ArrayList<Email>();
            ListTag emailTagList = (ListTag) inbox.get("Emails");
            for (int j = 0; j < emailTagList.size(); j++)
            {
                CompoundTag emailTag = emailTagList.getCompound(j);
                Email email = Email.readFromNBT(emailTag);
                emails.add(email);
            }
            nameToInbox.put(name, emails);
        }

        uuidToName.clear();

        ListTag accounts = (ListTag) nbt.get("Accounts");
        for (int i = 0; i < accounts.size(); i++)
        {
            CompoundTag account = accounts.getCompound(i);
            UUID uuid = UUID.fromString(account.getString("UUID"));
            String name = account.getString("Name");
            uuidToName.put(uuid, name);
        }
    }

    public void writeToNBT(CompoundTag nbt)
    {
        ListTag inboxes = new ListTag();
        for (String key : nameToInbox.keySet())
        {
            CompoundTag inbox = new CompoundTag();
            inbox.putString("Name", key);

            ListTag emailTagList = new ListTag();
            List<Email> emails = nameToInbox.get(key);
            for (Email email : emails)
            {
                CompoundTag emailTag = new CompoundTag();
                email.writeToNBT(emailTag);
                emailTagList.add(emailTag);
            }
            inbox.put("Emails", emailTagList);
            inboxes.add(inbox);
        }
        nbt.put("Inboxes", inboxes);

        ListTag accounts = new ListTag();
        for (UUID key : uuidToName.keySet())
        {
            CompoundTag account = new CompoundTag();
            account.putString("UUID", key.toString());
            account.putString("Name", uuidToName.get(key));
            accounts.add(account);
        }
        nbt.put("Accounts", accounts);
    }

    public void clear()
    {
        nameToInbox.clear();
        uuidToName.clear();
        inbox.clear();
    }

    private void sendNotification(String name, Email email)
    {
       MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        UUID id = uuidToName.inverse().get(name);
        if(id != null)
        {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if(player != null)
            {
                Notification notification = new Notification(Icons.MAIL, "New Email!", "from " + email.getAuthor());
                notification.pushTo(player);
            }
        }
    }
}
