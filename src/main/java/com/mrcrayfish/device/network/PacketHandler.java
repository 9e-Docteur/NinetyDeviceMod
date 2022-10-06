package com.mrcrayfish.device.network;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.network.task.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler
{
	public static final SimpleChannel simpleChannel;

	public static void init()
	{
		SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Reference.MOD_ID, "messages")).networkProtocolVersion(() -> "1.0").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();
		INSTANCE = simpleChannel;
		INSTANCE.registerMessage(1, MessageRequest.class, MessageRequest::toBytes, MessageRequest::new, MessageRequest::onMessage);
		INSTANCE.registerMessage(MessageResponse.class, MessageResponse.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncApplications.class, MessageSyncApplications.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncConfig.class, MessageSyncConfig.class, 4, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncBlock.class, MessageSyncBlock.class, 5, Side.SERVER);
		INSTANCE.registerMessage(MessageNotification.class, MessageNotification.class, 6, Side.CLIENT);
	}
}
