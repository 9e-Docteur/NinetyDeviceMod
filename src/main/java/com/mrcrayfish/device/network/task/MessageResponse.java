package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.network.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageResponse implements IPacket<MessageResponse> {
	private int id;
	private Task request;
	private CompoundTag tag;


	public MessageResponse() {

	}

	public MessageResponse(int id, Task request) {
		this.id = id;
		this.request = request;
	}

	@Override
	public void encode(MessageResponse packet, FriendlyByteBuf byteBuf) {
		byteBuf.writeInt(this.id);
		byteBuf.writeBoolean(this.request.isSucessful());
		byteBuf.writeUtf(this.request.getName());
		CompoundTag tag = new CompoundTag();
		this.request.prepareResponse(tag);
		byteBuf.writeNbt(tag);
		this.request.complete();
	}

	@Override
	public MessageResponse decode(FriendlyByteBuf byteBuf) {
		this.id = byteBuf.readInt();
		boolean successful = byteBuf.readBoolean();
		this.request = TaskManager.getTaskAndRemove(this.id);
		if (successful) this.request.setSuccessful();
		String name = byteBuf.readUtf();
		this.tag = byteBuf.readNbt();
		return null;
	}

	@Override
	public void handlePacket(MessageResponse packet, Supplier<NetworkEvent.Context> ctx) {
		request.processResponse(tag);
		request.callback(tag);
	}
}
