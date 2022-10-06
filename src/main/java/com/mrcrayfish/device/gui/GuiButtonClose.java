package com.mrcrayfish.device.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.core.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GuiButtonClose extends Button
{
	public GuiButtonClose(int x, int y) {
		super(x, y, 11, 11, Component.empty(), (button) -> {

		});
	}

	@Override
	public void renderButton(@NotNull PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			Font font = Minecraft.getInstance().font;
			RenderSystem.setShaderTexture(0, Window.WINDOW_GUI);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.blendFunc(770, 771);

			int state = this.isHovered ? 1 : 0;
			blit(pose, this.x, this.y, state * this.width + 15, 0, this.width, this.height);
		}
	}

	public boolean isHovered() {
		return isHovered;
	}
}
