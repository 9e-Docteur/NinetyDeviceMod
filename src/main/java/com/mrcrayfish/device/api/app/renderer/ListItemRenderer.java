package com.mrcrayfish.device.api.app.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.core.Device;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;

public abstract class ListItemRenderer<E> 
{
	private final int height;
	
	public ListItemRenderer(int height) 
	{
		this.height = height;
	}
	
	public final int getHeight() 
	{
		return height;
	}

	public void render(PoseStack poseStack, E e, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {

	}

	public void render(PoseStack poseStack, AppInfo info, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {

	}

	public void render(PoseStack poseStack, Drive drive, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {

	}

	public void render(PoseStack poseStack, File file, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {

	}

	public void render(PoseStack poseStack, Device device, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {

	}

	public void render(PoseStack poseStack, E e, GuiComponent gui, Minecraft mc, int x, int y, int width, int height, boolean selected){

	}
}
