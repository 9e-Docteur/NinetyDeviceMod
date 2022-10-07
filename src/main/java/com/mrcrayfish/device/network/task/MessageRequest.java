package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.network.IPacket;
import com.mrcrayfish.device.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequest implements IPacket<MessageRequest>
{
	private int id;
	private Task request;
	private CompoundTag nbt;

	public MessageRequest() {}

	public MessageRequest(int id, Task request)
	{
		this.id = id;
		this.request = request;
	}

	public int getId()
	{
		return id;
	}


	@Override
	public void encode(MessageRequest packet, FriendlyByteBuf byteBuf) {
		byteBuf.writeInt(this.id);
		byteBuf.writeUtf(this.request.getName());
		CompoundTag tag = new CompoundTag();
		this.request.prepareRequest(tag);
		byteBuf.writeNbt(tag);
	}

	@Override
	public MessageRequest decode(FriendlyByteBuf byteBuf) {
		this.id = byteBuf.readInt();
		String name = byteBuf.readUtf();
		this.request = TaskManager.getTask(name);
		this.nbt = byteBuf.readNbt();
		return null;
	}

	@Override
	public void handlePacket(MessageRequest packet, Supplier<NetworkEvent.Context> ctx) {
		packet.request.processRequest(packet.nbt, ctx.get().getSender().level, ctx.get().getSender());
			PacketHandler.sendTo(new MessageRequest(id, request), ctx.get().getSender());
	}
}
