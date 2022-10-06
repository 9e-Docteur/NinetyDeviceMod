package com.mrcrayfish.device.network;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.network.task.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketHandler
{
	private static int id = 0;
	private static final String PROTOCOL_VERSION = Integer.toString(1);

	private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MOD_ID, "main_channel"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void init()
	{
	//	INSTANCE.registerMessage(1, MessageRequest.class, MessageRequest::toBytes, MessageRequest::new, MessageRequest::onMessage);
	//	INSTANCE.registerMessage(MessageResponse.class, MessageResponse.class, 2, Side.CLIENT);
	//	INSTANCE.registerMessage(MessageSyncApplications.class, MessageSyncApplications.class, 3, Side.CLIENT);
	//	INSTANCE.registerMessage(MessageSyncConfig.class, MessageSyncConfig.class, 4, Side.CLIENT);
	//	INSTANCE.registerMessage(MessageSyncBlock.class, MessageSyncBlock.class, 5, Side.SERVER);

		registerMessage(MessageNotification.class, new MessageNotification());
	}

	// Needed for registering the packets
	private static <T extends IPacket<T>> void registerMessage(Class<T> tClass, IPacket<T> packetInstance) {
		INSTANCE.registerMessage(id++, tClass, packetInstance::encode, packetInstance::decode, packetInstance::handle);
	}

	public static <T extends IPacket<T>> void sendToAllClients(T packet) {
		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(p -> sendTo(p, packet));
	}

	public static <T extends IPacket<T>> void sendTo(T packet, ServerPlayer playerEntity) {
		INSTANCE.sendTo(packet, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <T extends IPacket<T>> void sendServerPacket(T packet) {
		INSTANCE.sendToServer(packet);
	}
}
