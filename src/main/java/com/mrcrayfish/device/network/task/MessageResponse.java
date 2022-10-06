package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class MessageResponse implements PacketListener, Packet<MessageResponse> {
	private final int id;
	private final Task request;
	private CompoundTag tag;

	public MessageResponse(FriendlyByteBuf buf) {
		this.id = buf.readInt();
		boolean successful = buf.readBoolean();
		this.request = TaskManager.getTaskAndRemove(this.id);
		if (successful) this.request.setSuccessful();
		String name = buf.readUtf();
		this.tag = buf.readNbt();
	}

	public MessageResponse(int id, Task request) {
		this.id = id;
		this.request = request;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeBoolean(this.request.isSucessful());
		buf.writeUtf(this.request.getName());
		CompoundTag tag = new CompoundTag();
		this.request.prepareResponse(tag);
		buf.writeNbt(tag);
		this.request.complete();
	}

	@Override
	public void handle(MessageResponse p_131342_) {
		request.processResponse(tag);
		request.callback(tag);
	}

	@Override
	public void onDisconnect(Component p_130552_) {

	}

	@Override
	public Connection getConnection() {
		return null;
	}
}
