package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.network.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequest implements IPacket<MessageRequest>
{

	//TODO: FIX ERROR IN ENCODE RECODE
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
		byteBuf.writeInt(this.id);
		byteBuf.writeUtf(this.request.getName());
		CompoundTag tag = new CompoundTag();
		this.request.prepareRequest(tag);
		byteBuf.writeNbt(tag);
		return null;
	}

	@Override
	public void handlePacket(MessageRequest packet, Supplier<NetworkEvent.Context> ctx) {
		packet.request.processRequest(packet.nbt, ctx.get().getSender().level, ctx.get().getSender().connection.player);
	}
}
