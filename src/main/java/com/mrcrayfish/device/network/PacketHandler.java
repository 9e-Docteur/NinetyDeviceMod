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
		registerMessage(MessageNotification.class, new MessageNotification());
		registerMessage(MessageRequest.class, new MessageRequest());
		registerMessage(MessageResponse.class, new MessageResponse());
		registerMessage(MessageSyncApplications.class, new MessageSyncApplications());
		registerMessage(MessageSyncBlock.class, new MessageSyncBlock());
		registerMessage(MessageSyncConfig.class, new MessageSyncConfig());
	}

	// Needed for registering the packets
	private static <T extends IPacket<T>> void registerMessage(Class<T> tClass, IPacket<T> packetInstance) {
		INSTANCE.registerMessage(id++, tClass, packetInstance::encode, packetInstance::decode, packetInstance::handle);
	}

	public static <T extends IPacket<T>> void sendToAllClients(T packet) {
		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(p -> sendTo(packet, p));
	}

	public static <T extends IPacket<T>> void sendTo(T packet, ServerPlayer playerEntity) {
		INSTANCE.sendTo(packet, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <T extends IPacket<T>> void sendServerPacket(T packet) {
		INSTANCE.sendToServer(packet);
	}
}
